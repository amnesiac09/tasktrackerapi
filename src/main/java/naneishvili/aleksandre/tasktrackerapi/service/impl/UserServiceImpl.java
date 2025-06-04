package naneishvili.aleksandre.tasktrackerapi.service.impl;

import naneishvili.aleksandre.tasktrackerapi.dto.request.UserRegistrationRequest;
import naneishvili.aleksandre.tasktrackerapi.dto.response.UserResponse;
import naneishvili.aleksandre.tasktrackerapi.entity.User;
import naneishvili.aleksandre.tasktrackerapi.exception.BadRequestException;
import naneishvili.aleksandre.tasktrackerapi.exception.ResourceNotFoundException;
import naneishvili.aleksandre.tasktrackerapi.mapper.UserMapper;
import naneishvili.aleksandre.tasktrackerapi.repository.UserRepository;
import naneishvili.aleksandre.tasktrackerapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {
        if (existsByEmail(request.getEmail())) {
            throw new BadRequestException("User with email " + request.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}