package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.movie.MovieRequest;
import org.example.dto.movie.MovieUpdateRequest;
import org.example.entity.constants.MovieCategory;
import org.example.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/add/movie")
    public ResponseEntity<Map<String,Object>> addMovie(@RequestBody @Valid MovieRequest request){
        return movieService.addMovie(request);
    }

    @GetMapping("/view/movies/page/{page}/size/{size}")
    public ResponseEntity<Map<String,Object>> getAllMovies(@PathVariable Integer page, @PathVariable Integer size){
        return movieService.getAllMovies(page,size);
    }

    @PutMapping("/update/movie/{id}")
    public ResponseEntity<Map<String,Object>> updateMovie(@PathVariable Integer id, @RequestBody @Valid MovieUpdateRequest updateRequest){
        return movieService.updateMovie(id,updateRequest);
    }

    @DeleteMapping("/delete/movie/{id}")
    public ResponseEntity<String> deleteMovieById(@PathVariable Integer id){

        return movieService.deleteMovieById(id);
    }

    @GetMapping("/search-by-id/{id}")
    public ResponseEntity<MovieRequest> searchMovieById(@PathVariable Integer id){
        return movieService.searchMovieById(id);
    }

    @GetMapping("/search-by-name/{name}")
    public ResponseEntity<MovieRequest> searchMovieByName(@PathVariable String name){
        return movieService.searchMovieByName(name);
    }

    @GetMapping("/search-by-category/{category}")
    public ResponseEntity<Map<String,Object>> searchMovieByCategory(@PathVariable MovieCategory category){
        return movieService.searchMovieByCategory(category);
    }
}
