package naneishvili.aleksandre.tasktrackerapi.controller;

import naneishvili.aleksandre.tasktrackerapi.dto.request.UserLoginRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.request.UserRegistrationRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.JwtResponse;
import naneishvili.aleksandre.tasktrackerapi.dto.response.UserResponse;
import naneishvili.aleksandre.tasktrackerapi.service.AuthService;
import naneishvili.aleksandre.tasktrackerapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userService.registerUser(request);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody UserLoginRequest request) {
        JwtResponse jwtResponse = authService.login(request);
        return ResponseEntity.ok(jwtResponse);
    }
}