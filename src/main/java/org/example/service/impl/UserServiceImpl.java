package org.example.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.LoginRequest;
import org.example.dto.user.LoginResponse;
import org.example.dto.user.UserRequest;
import org.example.dto.user.UserUpdateRequest;
import org.example.entity.TokenEntity;
import org.example.entity.UserEntity;
import org.example.entity.constants.Role;
import org.example.entity.constants.TokenType;
import org.example.exception.InvalidParameterException;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.UserExistsException;
import org.example.repository.SuggestionRepository;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.example.security.JWTUtill;
import org.example.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SuggestionRepository suggestionRepository;
    private final MessageSource source;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final JWTUtill jwtUtill;

    @Override
    public ResponseEntity<Map<String,Object>> RegisterUser(UserRequest request) {

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new UserExistsException(
                    source.getMessage("Email already exists",null,Locale.ENGLISH));
        }

        if(request.getPassword().length() < 6 || request.getPassword().length()>10){
            throw new InvalidParameterException(
                    source.getMessage("Invalid password length",null,Locale.ENGLISH));
        }

        UserEntity userEntity = objectMapper.convertValue(request, UserEntity.class);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(request.getPassword());

        userEntity.setPassword(encodePassword);
        userEntity.setRole(Role.USER);

        UserEntity saveUser = userRepository.save(userEntity);

        TokenEntity tokenEntity = new TokenEntity();

        String token = jwtUtill.generateToken(saveUser, TokenType.VERIFICATION);
        tokenEntity.setToken(token);
        tokenEntity.setTokenType(TokenType.VERIFICATION);
        tokenEntity.setRevoked(false);
        tokenEntity.setExpired(false);
        tokenEntity.setUser(saveUser);

        tokenRepository.save(tokenEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration complete.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LoginResponse> loginUSer(LoginRequest loginRequest) {
        Optional<UserEntity> user = userRepository.findByEmail(loginRequest.getEmail());

        if(user.isEmpty()){
            throw new InvalidParameterException(
                    source.getMessage("Invalid Username or Password. Please try again.", null, Locale.ENGLISH));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (AuthenticationException ex) {
            throw new InvalidParameterException(
                    source.getMessage("Invalid Username or Password. Please try again.",
                            null, Locale.ENGLISH));
        }

        List<TokenEntity> tokenEntityList = tokenRepository.findAllByUserAndTokenTypeAndExpiredFalseAndRevokedFalse(user, TokenType.BEARER);

        for(TokenEntity token:tokenEntityList){
            token.setExpired(true);
            token.setRevoked(true);
        }

        tokenRepository.saveAll(tokenEntityList);

        String token = jwtUtill.generateToken(user.get(), TokenType.BEARER);

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setTokenType(TokenType.BEARER);
        tokenEntity.setRevoked(false);
        tokenEntity.setExpired(false);
        tokenEntity.setUser(user.get());

        tokenRepository.save(tokenEntity);

        return ResponseEntity.ok(
                new LoginResponse(user.get().getId(), tokenEntity.getToken())
        );
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllUsers(Role role) {
        List<UserEntity> users = userRepository.findAllByRoleIsNot(Role.ADMIN);

        List<UserRequest> userDTOs = users.stream()
                .map(this::mapEntityToRequest)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOs);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateUser(Integer id, UserUpdateRequest updateRequest) {

        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        source.getMessage("User not found", null, Locale.ENGLISH)));

        if (!existingUser.getEmail().equals(updateRequest.getEmail()) &&
                userRepository.findByEmail(updateRequest.getEmail()).isPresent()) {
            throw new UserExistsException(
                    source.getMessage("Email already exists", null, Locale.ENGLISH));
        }

        try {
            objectMapper.updateValue(existingUser, updateRequest);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
        userRepository.save(existingUser);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> deleteUserById(Integer id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new InvalidParameterException("Cannot delete admin user");
        }

        suggestionRepository.deleteAllByUser(user);

        List<TokenEntity> userTokens = tokenRepository.findAllByUser(user);
        if (!userTokens.isEmpty()) {
            tokenRepository.deleteAll(userTokens);
        }

        userRepository.delete(user);

        return ResponseEntity.ok("User and all related data deleted successfully");
    }

    @Override
    public ResponseEntity<UserRequest> searchUserById(Integer id) {

        if (id == null) {
            throw new InvalidParameterException(
                    source.getMessage("User ID cannot be null", null, Locale.ENGLISH));
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        source.getMessage("User not found", null, Locale.ENGLISH)));

        UserRequest userDto = mapEntityToRequest(user);
        return ResponseEntity.ok(userDto);
    }

    @Override
    public ResponseEntity<UserRequest> searchUserByMail(String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidParameterException(
                    source.getMessage("Email cannot be empty", null, Locale.ENGLISH));
        }

        UserEntity user = userRepository.findByEmailContainingIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        source.getMessage("User not found", null, Locale.ENGLISH)));

        UserRequest userDto = mapEntityToRequest(user);
        return ResponseEntity.ok(userDto);
    }

    private UserRequest mapEntityToRequest(UserEntity entity) {
        UserRequest request = new UserRequest();
        request.setId(entity.getId());
        request.setName(entity.getName());
        request.setEmail(entity.getEmail());
        request.setPassword(entity.getPassword());
        request.setRole(entity.getRole());
        request.setCreatedAt(entity.getCreatedAt());
        request.setUpdatedAt(entity.getUpdatedAt());

        return request;
    }
}