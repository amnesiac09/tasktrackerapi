package naneishvili.aleksandre.tasktrackerapi.service.impl;

import naneishvili.aleksandre.tasktrackerapi.dto.request.UserLoginRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.JwtResponse;
import naneishvili.aleksandre.tasktrackerapi.dto.response.UserResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.User;
import naneishvili.aleksandre.tasktrackerapi.enums.Role;
import naneishvili.aleksandre.tasktrackerapi.mapper.UserMapper;
import naneishvili.aleksandre.tasktrackerapi.security.JwtUtil;
import naneishvili.aleksandre.tasktrackerapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserLoginRequest loginRequest;
    private User testUser;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");
        userResponse.setRole(Role.USER);
    }

    @Test
    void login_ValidCredentials_ReturnsJwtResponse() {
        String token = "jwt-token";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtUtil.generateToken("test@example.com")).thenReturn(token);
        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        JwtResponse result = authService.login(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void generateToken_ReturnsToken() {
        String expectedToken = "jwt-token";
        when(jwtUtil.generateToken("test@example.com")).thenReturn(expectedToken);

        String result = authService.generateToken("test@example.com");

        assertThat(result).isEqualTo(expectedToken);
    }

    @Test
    void extractUsername_ReturnsUsername() {
        String token = "jwt-token";
        String expectedUsername = "test@example.com";
        when(jwtUtil.extractUsername(token)).thenReturn(expectedUsername);

        String result = authService.extractUsername(token);

        assertThat(result).isEqualTo(expectedUsername);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = "jwt-token";
        String username = "test@example.com";
        when(jwtUtil.validateToken(token, username)).thenReturn(true);

        boolean result = authService.validateToken(token, username);

        assertThat(result).isTrue();
    }
}