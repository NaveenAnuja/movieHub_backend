package org.example.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.dto.movie.MovieRequest;
import org.example.dto.movie.MovieUpdateRequest;
import org.example.entity.MovieEntity;
import org.example.entity.constants.MovieCategory;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.MovieRepository;
import org.example.service.MovieService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;
    private final MessageSource source;

    @Override
    public ResponseEntity<Map<String, Object>> addMovie(MovieRequest request) {
        validateImageUrl(request.getImageUrl());

        MovieEntity movie = new MovieEntity();
        movie.setMovieName(request.getMovieName());
        movie.setDescription(request.getDescription());
        movie.setRate(request.getRate());
        movie.setCategory(request.getCategory());
        movie.setImageUrl(request.getImageUrl());

        movieRepository.save(movie);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Movie added successfully");
        response.put("movie", objectMapper.convertValue(movie, MovieRequest.class));

        return ResponseEntity.ok(response);
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException(source.getMessage("Image URL cannot be empty", null, Locale.ENGLISH));
        }

        // Optional: Basic format validation
        if (!imageUrl.matches("^(http|https)://.*$")) {
            throw new IllegalArgumentException(source.getMessage("Invalid image URL format", null, Locale.ENGLISH));
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllMovies(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieEntity> moviePage = movieRepository.findAll(pageable);

        List<MovieRequest> movieRequests = moviePage.getContent().stream()
                .map(movie -> objectMapper.convertValue(movie, MovieRequest.class))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("movies", movieRequests);
        response.put("currentPage", moviePage.getNumber());
        response.put("totalPages", moviePage.getTotalPages());
        response.put("totalItems", moviePage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateMovie(Integer id, MovieUpdateRequest request) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        source.getMessage("Movie not found", null, Locale.ENGLISH)
                ));

        validateImageUrl(request.getImageUrl());

        movie.setMovieName(request.getMovieName());
        movie.setDescription(request.getDescription());
        movie.setRate(request.getRate());
        movie.setImageUrl(request.getImageUrl());

        MovieEntity updatedMovie = movieRepository.save(movie);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Movie updated successfully");
        response.put("movie", objectMapper.convertValue(updatedMovie, MovieRequest.class));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MovieRequest> searchMovieById(Integer id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        source.getMessage("Movie not found", null, Locale.ENGLISH)
                ));

        MovieRequest movieRequest = objectMapper.convertValue(movie, MovieRequest.class);
        return ResponseEntity.ok(movieRequest);
    }

    @Override
    public ResponseEntity<String> deleteMovieById(Integer id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        source.getMessage("Movie not found", null, Locale.ENGLISH)
                ));

        movieRepository.delete(movie);
        return ResponseEntity.ok("Movie deleted successfully");
    }

    @Override
    public ResponseEntity<MovieRequest> searchMovieByName(String name) {
        MovieEntity movie = movieRepository.findByMovieNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        source.getMessage("Movie not found", null, Locale.ENGLISH)
                ));

        MovieRequest movieRequest = objectMapper.convertValue(movie, MovieRequest.class);
        return ResponseEntity.ok(movieRequest);
    }

    @Override
    public ResponseEntity<Map<String, Object>> searchMovieByCategory(MovieCategory category) {
        List<MovieEntity> movies = movieRepository.findByCategoryOrderByCreatedAtDesc(category);

        if (movies.isEmpty()) {
            throw new ResourceNotFoundException(
                    source.getMessage("No movies found for category", null, Locale.ENGLISH)
            );
        }

        List<MovieRequest> movieRequests = movies.stream()
                .map(movie -> objectMapper.convertValue(movie, MovieRequest.class))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("movies", movieRequests);
        response.put("category", category);
        response.put("totalCount", movies.size());

        return ResponseEntity.ok(response);
    }
}