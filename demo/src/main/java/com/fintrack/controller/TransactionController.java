package com.fintrack.controller;

import com.fintrack.dto.TransactionDto;
import com.fintrack.entity.Transaction;
import com.fintrack.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDto transactionDTO) {
        transactionService.createTransaction(
                transactionDTO.getUserId(),
                transactionDTO.getTag(),
                transactionDTO.getCategory(),
                transactionDTO.getDescription(),
                transactionDTO.getAmount(),
                transactionDTO.getTransactionDate(),
                transactionDTO.isRecurring(),
                transactionDTO.getRecurrencePattern()
                );
        return ResponseEntity.ok("Transaction created successfully");
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return transactions.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable String id, @RequestBody TransactionDto transactionDTO) {
        Optional<Transaction> updateTransaction = transactionService.updateTransaction(
                id,
                transactionDTO.getUserId(),
                transactionDTO.getTag(),
                transactionDTO.getCategory(),
                transactionDTO.getDescription(),
                transactionDTO.getAmount(),
                transactionDTO.getTransactionDate(),
                transactionDTO.isRecurring(),
                transactionDTO.getRecurrencePattern()
        );
        return updateTransaction.map(transaction -> ResponseEntity.ok("Transaction updated successfully"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String id) {
        if(transactionService.deleteTransaction(id)) {
            return ResponseEntity.ok("Transaction deleted successfully");
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}

