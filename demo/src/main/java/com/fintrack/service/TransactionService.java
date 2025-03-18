package com.fintrack.service;

import com.fintrack.entity.Transaction;
import com.fintrack.entity.User;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
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
    private final UserRepository userRepository; // Inject UserRepository
    private final BudgetComparisonService budgetComparisonService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, BudgetComparisonService budgetComparisonService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.budgetComparisonService = budgetComparisonService;
    }

    public Transaction createTransaction(String userId, Tag tag, Category category, String description, double amount,
                                         LocalDateTime transactionDate, boolean isRecurring, RecurrenceFrequency recurrenceFrequency) {
        // Fetch the user from the repository
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            // If the user does not exist, throw an exception or handle the error as needed
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userOptional.get(); // Get the user object

        // Create the new transaction object
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);  // Set userId
        transaction.setUser(user); // Set the User object (Important!)

        // Set other properties
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

        // Save the transaction
        transaction = transactionRepository.save(transaction);

        // Now that the user is set, check if the budget is exceeded
        budgetComparisonService.checkBudgetExceedance(transaction.getUser(), transaction.getTransactionDate().getYear());

        return transaction;
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

    public List<Transaction> getRecurringTransactions() {
        return transactionRepository.findByIsRecurringTrue();
    }

    public void createRecurringTransaction(Transaction originalTransaction) {
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

        originalTransaction.setNextRecurrenceDate(nextDate);
        transactionRepository.save(originalTransaction);
    }
}