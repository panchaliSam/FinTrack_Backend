package com.fintrack.service;

import com.fintrack.entity.Budget;
import com.fintrack.entity.Transaction;
import com.fintrack.entity.User;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.type.Category;
import com.fintrack.type.Role;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetComparisonService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public BudgetComparisonService(BudgetRepository budgetRepository,
                                   TransactionRepository transactionRepository,
                                   UserRepository userRepository,
                                   EmailService emailService) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void checkBudgetExceedance(User user, int currentYear) {
        LocalDateTime yearStart = LocalDateTime.of(currentYear, Month.JANUARY, 1, 0, 0, 0, 0);
        LocalDateTime yearEnd = LocalDateTime.of(currentYear + 1, Month.JANUARY, 1, 0, 0, 0, 0);

        List<Budget> budgets = budgetRepository.findByUserIdAndYear(user.getId(), yearStart, yearEnd);

        List<Transaction> transactions = transactionRepository.findByUserIdAndYear(user.getId(), yearStart, yearEnd);

        double totalIncomeBudget = budgets.stream()
                .filter(budget -> budget.getBudgetCategory() == Category.INCOME)
                .mapToDouble(Budget::getAmount)
                .sum();

        double totalExpenseBudget = budgets.stream()
                .filter(budget -> budget.getBudgetCategory() == Category.EXPENSE)
                .mapToDouble(Budget::getAmount)
                .sum();

        double totalIncomeTransactions = transactions.stream()
                .filter(tx -> tx.getCategory() == Category.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenseTransactions = transactions.stream()
                .filter(tx -> tx.getCategory() == Category.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        if (totalIncomeTransactions > totalIncomeBudget || totalExpenseTransactions > totalExpenseBudget) {
            notifyAdmin(user, totalIncomeTransactions, totalExpenseTransactions, totalIncomeBudget, totalExpenseBudget, currentYear);
        }
    }

    private void notifyAdmin(User user, double totalIncome, double totalExpense,
                             double incomeBudget, double expenseBudget, int year) {

        List<String> adminEmails = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .map(User::getEmail)
                .collect(Collectors.toList());

        if (!adminEmails.isEmpty()) {
            String subject = "Budget Exceedance Alert for User: " + user.getId();
            StringBuilder body = new StringBuilder("<h2>Budget Exceedance Alert</h2>");

            String userName = user.getFirstName() + " " + user.getLastName();
            String userEmail = user.getEmail();

            body.append("<p><strong>User Name:</strong> ").append(userName).append("</p>");
            body.append("<p><strong>User Email:</strong> ").append(userEmail).append("</p>");
            body.append("<p><strong>Year:</strong> ").append(year).append("</p>");

            DecimalFormat df = new DecimalFormat("#,###.00");

            if (totalIncome > incomeBudget) {
                body.append("<p style='color:green;'><strong>Income Exceeded:</strong> Budget: ")
                        .append(df.format(incomeBudget))
                        .append(", Transactions: ")
                        .append(df.format(totalIncome))
                        .append("</p>");
            }

            if (totalExpense > expenseBudget) {
                body.append("<p style='color:red;'><strong>Expense Exceeded:</strong> Budget: ")
                        .append(df.format(expenseBudget))
                        .append(", Transactions: ")
                        .append(df.format(totalExpense))
                        .append("</p>");
            }

            emailService.sendEmailToMultipleRecipients(adminEmails, subject, body.toString());
        }
    }
}