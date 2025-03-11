package com.fintrack.repository;

import com.fintrack.entity.Savings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SavingsRepository extends MongoRepository<Savings, String> {
}
