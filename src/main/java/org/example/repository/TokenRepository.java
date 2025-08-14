package org.example.repository;

import org.example.entity.TokenEntity;
import org.example.entity.UserEntity;
import org.example.entity.constants.TokenType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, Integer> {
    Optional<TokenEntity> findByToken(String jwtToken);

    List<TokenEntity> findAllByUserAndTokenTypeAndExpiredFalseAndRevokedFalse(Optional<UserEntity> user, TokenType tokenType);

    List<TokenEntity> findAllByUser(UserEntity user);
}
