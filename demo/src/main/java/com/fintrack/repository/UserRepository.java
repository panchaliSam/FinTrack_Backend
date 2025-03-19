package com.fintrack.repository;

import com.fintrack.entity.User;
import com.fintrack.type.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query(value = "{}", fields = "{'_id' : 1}")
    List<String> findAllUserIds();
}
