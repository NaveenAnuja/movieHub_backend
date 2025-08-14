package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.LoginRequest;
import org.example.dto.user.LoginResponse;
import org.example.dto.user.UserRequest;
import org.example.dto.user.UserUpdateRequest;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

    @PostMapping("/register/user")
    public ResponseEntity<Map<String,Object>> RegisterUser(@RequestBody @Valid UserRequest request){
        return userService.RegisterUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest){
        return userService.loginUSer(loginRequest);
    }

    @GetMapping("/view/users")
    public ResponseEntity<Map<String,Object>> getAllUsers(){

        return userService.getAllUsers();
    }

    @PutMapping("/update/user/{id}")
    public ResponseEntity<Map<String,Object>> updateUser(@PathVariable  Integer id,@RequestBody @Valid UserUpdateRequest updateRequest){
        return userService.updateUser(id,updateRequest);
    }

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer id){
        return userService.deleteUserById(id);
    }

    @GetMapping("/search-by-id/{id}")
    public ResponseEntity<UserRequest> searchUserById(@PathVariable Integer id){
        return userService.searchUserById(id);
    }

    @GetMapping("/search-by-email/{email}")
    public ResponseEntity<UserRequest> searchUserByMail(@PathVariable String email){
        return userService.searchUserByMail(email);
    }
}
