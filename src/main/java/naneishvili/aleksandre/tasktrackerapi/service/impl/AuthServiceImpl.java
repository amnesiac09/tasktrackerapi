package naneishvili.aleksandre.tasktrackerapi.service.impl;

import naneishvili.aleksandre.tasktrackerapi.dto.request.UserLoginRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.JwtResponse;
import naneishvili.aleksandre.tasktrackerapi.dto.response.UserResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.User;
import naneishvili.aleksandre.tasktrackerapi.mapper.UserMapper;
import naneishvili.aleksandre.tasktrackerapi.security.JwtUtil;
import naneishvili.aleksandre.tasktrackerapi.service.AuthService;
import naneishvili.aleksandre.tasktrackerapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public JwtResponse login(UserLoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String token = generateToken(request.getEmail());

            User user = userService.findByEmail(request.getEmail());
            UserResponse userResponse = userMapper.toResponse(user);

            return new JwtResponse(token, userResponse);

        } catch (AuthenticationException e) {
            throw e;
        }
    }

    @Override
    public String generateToken(String email) {
        return jwtUtil.generateToken(email);
    }

    @Override
    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }

    @Override
    public boolean validateToken(String token, String username) {
        return jwtUtil.validateToken(token, username);
    }
}