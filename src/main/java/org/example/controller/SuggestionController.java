package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.suggestion.SuggestionRequest;
import org.example.dto.suggestion.SuggestionUpdateRequest;
import org.example.service.SuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/suggestion")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService service;

    @PostMapping("/add/suggestions")
    public ResponseEntity<Map<String,Object>> addSuggestion(@RequestBody @Valid SuggestionRequest request){
        return service.addSuggestion(request);
    }

    @PutMapping("/update/suggestion/{id}")
    public ResponseEntity<Map<String,Object>> updateSuggestion(@PathVariable Integer id, @RequestBody @Valid SuggestionUpdateRequest updateRequest){
        return service.updateSuggestion(id,updateRequest);
    }

    @DeleteMapping("/delete/suggestion/{id}")
    public ResponseEntity<String> deleteSuggestionById(@PathVariable Integer id){
        return service.deleteSuggestionById(id);
    }

    @GetMapping("/view/suggestions")
    public ResponseEntity<List<SuggestionRequest>> viewSuggestions(){
        return service.viewSuggestions();
    }

    @GetMapping("/search/suggestion-by-id/{id}")
    public ResponseEntity<SuggestionRequest> searchSuggestionById(@PathVariable Integer id){
        return service.searchSuggestionById(id);
    }

}
