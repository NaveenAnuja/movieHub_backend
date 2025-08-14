package org.example.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.dto.suggestion.SuggestionRequest;
import org.example.dto.suggestion.SuggestionUpdateRequest;
import org.example.dto.user.UserRequest;
import org.example.dto.movie.MovieRequest;
import org.example.entity.MovieEntity;
import org.example.entity.SuggestionEntity;
import org.example.entity.UserEntity;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.MovieRepository;
import org.example.repository.SuggestionRepository;
import org.example.repository.UserRepository;
import org.example.service.SuggestionService;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SuggestionServiceImpl implements SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;
    private final MessageSource source;

    @Override
    public ResponseEntity<Map<String, Object>> addSuggestion(SuggestionRequest request) {

        UserEntity user = userRepository.findById(request.getUserRequest().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserRequest().getId()));

        MovieEntity movie = movieRepository.findById(request.getMovieRequest().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + request.getMovieRequest().getId()));

        SuggestionEntity suggestion = new SuggestionEntity();
        suggestion.setComment(request.getComment());
        suggestion.setUser(user);
        suggestion.setMovie(movie);

        suggestionRepository.save(suggestion);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Suggestion added successfully");

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateSuggestion(Integer id, SuggestionUpdateRequest updateRequest) {
        Map<String, Object> response = new HashMap<>();

        SuggestionEntity existing = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found with ID: " + id));

        existing.setComment(updateRequest.getComment());
        suggestionRepository.save(existing);

        response.put("message", "Suggestion updated successfully");

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> deleteSuggestionById(Integer id) {
        SuggestionEntity existing = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found with ID: " + id));

        suggestionRepository.delete(existing);

        return ResponseEntity.ok("Suggestion deleted successfully");
    }

    @Override
    public ResponseEntity<List<SuggestionRequest>> viewSuggestions() {
        List<SuggestionEntity> list = suggestionRepository.findAll();
        List<SuggestionRequest> suggestions = list.stream()
                .map(this::mapEntityToRequest)
                .collect(Collectors.toList());
        return ResponseEntity.ok(suggestions);
    }

    @Override
    public ResponseEntity<SuggestionRequest> searchSuggestionById(Integer id) {
        SuggestionEntity suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found with ID: " + id));

        SuggestionRequest suggestionRequest = mapEntityToRequest(suggestion);

        return ResponseEntity.ok(suggestionRequest);
    }

    private SuggestionRequest mapEntityToRequest(SuggestionEntity entity) {
        SuggestionRequest request = new SuggestionRequest();
        request.setId(entity.getId());
        request.setComment(entity.getComment());
        request.setCreatedAt(entity.getCreatedAt());
        request.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getUser() != null) {
            UserRequest userRequest = objectMapper.convertValue(entity.getUser(), UserRequest.class);
            request.setUserRequest(userRequest);
        }

        if (entity.getMovie() != null) {
            MovieRequest movieRequest = objectMapper.convertValue(entity.getMovie(), MovieRequest.class);
            request.setMovieRequest(movieRequest);
        }

        return request;
    }
}