package com.fintrack.scheduler;

import com.fintrack.entity.Transaction;
import com.fintrack.service.BudgetComparisonService;
import com.fintrack.service.TransactionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BudgetCheckScheduler {

    private final BudgetComparisonService budgetComparisonService;
    private final TransactionService transactionService;

    public BudgetCheckScheduler(BudgetComparisonService budgetComparisonService, TransactionService transactionService) {
        this.budgetComparisonService = budgetComparisonService;
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void checkBudgetsDaily() {
        List<Transaction> allTransactions = transactionService.getAllTransactions();

        for (Transaction transaction : allTransactions) {
            if (transaction.getUser() != null) {
                int currentYear = LocalDateTime.now().getYear();
                budgetComparisonService.checkBudgetExceedance(transaction.getUser(), currentYear);
            }
        }
    }
}