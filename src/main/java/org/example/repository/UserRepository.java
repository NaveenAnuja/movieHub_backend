package org.example.repository;

import org.example.entity.UserEntity;
import org.example.entity.constants.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByEmailContainingIgnoreCase(String email);

    List<UserEntity> findAllByRoleIsNot(Role role);
}
