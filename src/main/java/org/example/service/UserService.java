package org.example.service;

import org.example.dto.user.LoginRequest;
import org.example.dto.user.LoginResponse;
import org.example.dto.user.UserRequest;
import org.example.dto.user.UserUpdateRequest;
import org.example.entity.constants.Role;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {
    ResponseEntity<Map<String,Object>> RegisterUser(UserRequest request);

    ResponseEntity<Map<String, Object>> getAllUsers(Role role);

    ResponseEntity<Map<String, Object>> updateUser(Integer id, UserUpdateRequest updateRequest);

    ResponseEntity<String> deleteUserById(Integer id);

    ResponseEntity<UserRequest> searchUserById(Integer id);

    ResponseEntity<UserRequest> searchUserByMail(String email);

    ResponseEntity<LoginResponse> loginUSer(LoginRequest loginRequest);

}
