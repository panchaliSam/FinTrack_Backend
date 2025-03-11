package com.fintrack.repository;

import com.fintrack.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByUserId(String userId);
    List<Transaction> findByUserIdAndTransactionDateBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
}
