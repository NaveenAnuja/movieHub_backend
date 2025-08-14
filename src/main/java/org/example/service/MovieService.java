package org.example.service;

import org.example.dto.movie.MovieRequest;
import org.example.dto.movie.MovieUpdateRequest;
import org.example.dto.user.UserRequest;
import org.example.entity.constants.MovieCategory;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface MovieService {
    ResponseEntity<Map<String, Object>> addMovie(MovieRequest request);

    ResponseEntity<Map<String, Object>> getAllMovies(Integer page, Integer size);

    ResponseEntity<Map<String, Object>> updateMovie(Integer id, MovieUpdateRequest updateRequest);

    ResponseEntity<MovieRequest> searchMovieById(Integer id);

    ResponseEntity<String> deleteMovieById(Integer id);

    ResponseEntity<MovieRequest> searchMovieByName(String name);

    ResponseEntity<Map<String,Object>> searchMovieByCategory(MovieCategory category);
}
