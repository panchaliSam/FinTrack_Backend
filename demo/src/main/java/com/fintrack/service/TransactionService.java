package com.fintrack.service;

import com.fintrack.entity.Transaction;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.type.Category;
import com.fintrack.type.Tag;
import com.fintrack.type.RecurrenceFrequency;
import com.fintrack.utility.RecurrenceUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(String userId, Tag tag, Category category, String description, double amount,
                                         LocalDateTime transactionDate, boolean isRecurring, RecurrenceFrequency recurrenceFrequency) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setTag(tag);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setTransactionDate(transactionDate);
        transaction.setRecurring(isRecurring);
        transaction.setRecurrenceFrequency(recurrenceFrequency);

        if (isRecurring && recurrenceFrequency != null) {
            LocalDateTime nextDate = RecurrenceUtil.calculateNextOccurrence(transactionDate, recurrenceFrequency);
            transaction.setNextRecurrenceDate(nextDate);
        } else {
            transaction.setNextRecurrenceDate(null);
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId);
    }

    public List<Transaction> getTransactionByUserId(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Optional<Transaction> updateTransaction(String transactionId, String userId, Tag tag, Category category,
                                                   String description, double amount, LocalDateTime transactionDate,
                                                   boolean isRecurring, RecurrenceFrequency recurrenceFrequency) {
        Optional<Transaction> existingTransaction = transactionRepository.findById(transactionId);
        if (existingTransaction.isPresent()) {
            Transaction transaction = existingTransaction.get();
            transaction.setUserId(userId);
            transaction.setTag(tag);
            transaction.setCategory(category);
            transaction.setDescription(description);
            transaction.setAmount(amount);
            transaction.setTransactionDate(transactionDate);
            transaction.setRecurring(isRecurring);
            transaction.setRecurrenceFrequency(recurrenceFrequency);

            if (isRecurring && recurrenceFrequency != null) {
                LocalDateTime nextDate = RecurrenceUtil.calculateNextOccurrence(transactionDate, recurrenceFrequency);
                transaction.setNextRecurrenceDate(nextDate);
            } else {
                transaction.setNextRecurrenceDate(null);
            }

            return Optional.of(transactionRepository.save(transaction));
        }
        return Optional.empty();
    }

    public boolean deleteTransaction(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isPresent()) {
            transactionRepository.deleteById(transactionId);
            return true;
        }
        return false;
    }
}