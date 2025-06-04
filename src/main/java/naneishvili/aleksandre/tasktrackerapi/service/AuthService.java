package naneishvili.aleksandre.tasktrackerapi.service;

import naneishvili.aleksandre.tasktrackerapi.dto.request.UserLoginRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.JwtResponse;

public interface AuthService {

    JwtResponse login(UserLoginRequest request);

    String generateToken(String email);

    String extractUsername(String token);

    boolean validateToken(String token, String username);
}