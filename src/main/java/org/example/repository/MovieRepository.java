package org.example.repository;

import org.example.entity.MovieEntity;
import org.example.entity.constants.MovieCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity,Integer> {

    List<MovieEntity> findByCategoryOrderByCreatedAtDesc(MovieCategory category);

    Optional<MovieEntity> findByMovieNameContainingIgnoreCase(String name);
}
