package naneishvili.aleksandre.tasktrackerapi.service;

import naneishvili.aleksandre.tasktrackerapi.dto.request.UserRegistrationRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.UserResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.User;

import java.util.List;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);

    User findByEmail(String email);

    User findById(Long id);

    List<UserResponse> getAllUsers();

    boolean existsByEmail(String email);
}