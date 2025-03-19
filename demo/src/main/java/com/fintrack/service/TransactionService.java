package com.fintrack.service;

import com.fintrack.entity.Transaction;
import com.fintrack.entity.User;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.type.Category;
import com.fintrack.type.Tag;
import com.fintrack.type.RecurrenceFrequency;
import com.fintrack.utility.RecurrenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetComparisonService budgetComparisonService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, BudgetComparisonService budgetComparisonService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.budgetComparisonService = budgetComparisonService;
    }

    public Transaction createTransaction(String userId, Tag tag, Category category, String description, double amount,
                                         LocalDateTime transactionDate, boolean isRecurring, RecurrenceFrequency recurrenceFrequency) {
        log.info("Creating transaction for user: {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            log.error("User not found with ID: {}", userId);
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setUser(user);
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
            log.info("Recurring transaction set with next recurrence date: {}", nextDate);
        } else {
            transaction.setNextRecurrenceDate(null);
        }

        transaction = transactionRepository.save(transaction);
        log.info("Transaction created successfully for user: {}", userId);

        budgetComparisonService.checkBudgetExceedance(transaction.getUser(), transaction.getTransactionDate().getYear());

        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        log.info("Fetching all transactions.");
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(String transactionId) {
        log.info("Fetching transaction with ID: {}", transactionId);
        return transactionRepository.findById(transactionId);
    }

    public List<Transaction> getTransactionByUserId(String userId) {
        log.info("Fetching transactions for user: {}", userId);
        return transactionRepository.findByUserId(userId);
    }

    public Optional<Transaction> updateTransaction(String transactionId, String userId, Tag tag, Category category,
                                                   String description, double amount, LocalDateTime transactionDate,
                                                   boolean isRecurring, RecurrenceFrequency recurrenceFrequency) {
        log.info("Updating transaction with ID: {}", transactionId);

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
                log.info("Updated recurring transaction with next recurrence date: {}", nextDate);
            } else {
                transaction.setNextRecurrenceDate(null);
            }

            transaction = transactionRepository.save(transaction);
            log.info("Transaction updated successfully with ID: {}", transactionId);
            return Optional.of(transaction);
        }

        log.error("Transaction with ID: {} not found.", transactionId);
        return Optional.empty();
    }

    public boolean deleteTransaction(String transactionId) {
        log.info("Attempting to delete transaction with ID: {}", transactionId);

        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isPresent()) {
            transactionRepository.deleteById(transactionId);
            log.info("Transaction with ID: {} deleted successfully.", transactionId);
            return true;
        }

        log.error("Transaction with ID: {} not found.", transactionId);
        return false;
    }

    public List<Transaction> getRecurringTransactions() {
        log.info("Fetching recurring transactions.");
        return transactionRepository.findByIsRecurringTrue();
    }

    public void createRecurringTransaction(Transaction originalTransaction) {
        log.info("Creating recurring transaction based on original transaction ID: {}", originalTransaction.getTransactionId());

        LocalDateTime nextDate = RecurrenceUtil.calculateNextOccurrence(originalTransaction.getNextRecurrenceDate(),
                originalTransaction.getRecurrenceFrequency());

        Transaction newTransaction = new Transaction();
        newTransaction.setUserId(originalTransaction.getUserId());
        newTransaction.setTag(originalTransaction.getTag());
        newTransaction.setCategory(originalTransaction.getCategory());
        newTransaction.setDescription(originalTransaction.getDescription());
        newTransaction.setAmount(originalTransaction.getAmount());
        newTransaction.setTransactionDate(LocalDateTime.now());
        newTransaction.setRecurring(true);
        newTransaction.setRecurrenceFrequency(originalTransaction.getRecurrenceFrequency());
        newTransaction.setNextRecurrenceDate(nextDate);

        transactionRepository.save(newTransaction);
        log.info("New recurring transaction created with ID: {}", newTransaction.getTransactionId());

        originalTransaction.setNextRecurrenceDate(nextDate);
        transactionRepository.save(originalTransaction);
        log.info("Updated original transaction with new next recurrence date: {}", nextDate);
    }
}
