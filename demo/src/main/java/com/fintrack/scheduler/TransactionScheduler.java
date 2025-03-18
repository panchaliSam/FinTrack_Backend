package com.fintrack.scheduler;

import com.fintrack.entity.Transaction;
import com.fintrack.service.TransactionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TransactionScheduler {

    private final TransactionService transactionService;

    public TransactionScheduler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void processRecurringTransactions() {
        List<Transaction> recurringTransactions = transactionService.getRecurringTransactions();

        for (Transaction transaction : recurringTransactions) {
            if (transaction.getNextRecurrenceDate() != null &&
                    transaction.getNextRecurrenceDate().isBefore(LocalDateTime.now())) {

                transactionService.createRecurringTransaction(transaction);
            }
        }
    }
}