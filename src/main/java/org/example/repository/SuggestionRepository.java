package org.example.repository;

import org.example.entity.SuggestionEntity;
import org.example.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionRepository extends JpaRepository<SuggestionEntity,Integer> {
    void deleteAllByUser(UserEntity user);
}
