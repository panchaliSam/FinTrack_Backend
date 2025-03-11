package com.fintrack.repository;

import com.fintrack.entity.User;
import com.fintrack.type.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
