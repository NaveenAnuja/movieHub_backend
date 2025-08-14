package org.example.service;

import org.example.dto.suggestion.SuggestionRequest;
import org.example.dto.suggestion.SuggestionUpdateRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface SuggestionService {

    ResponseEntity<Map<String, Object>> addSuggestion(SuggestionRequest request);

    ResponseEntity<Map<String, Object>> updateSuggestion(Integer id,SuggestionUpdateRequest updateRequest);

    ResponseEntity<String> deleteSuggestionById(Integer id);

    ResponseEntity<List<SuggestionRequest>> viewSuggestions();

    ResponseEntity<SuggestionRequest> searchSuggestionById(Integer id);
}
