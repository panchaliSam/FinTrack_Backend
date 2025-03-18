package com.fintrack.repository;

import com.fintrack.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByUserId(String userId);
    List<Transaction> findByUserIdAndTransactionDateBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByIsRecurringTrue();
    @Query("{'userId': ?0, 'transactionDate': { $gte: ?1, $lt: ?2 }}")
    List<Transaction> findByUserIdAndYear(String userId, LocalDateTime startDate, LocalDateTime endDate);
}
