package com.fintrack.service;

import com.fintrack.entity.Transaction;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.type.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public double calTotalIncome() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("category").is("INCOME")),
                Aggregation.group().sum("amount").as("totalIncome")
        );

        List<Map> results = mongoTemplate.aggregate(aggregation, Transaction.class, Map.class).getMappedResults();

        if (!results.isEmpty()) {
            Map result = results.get(0);
            return (double) result.get("totalIncome");
        }
        return 0.0;
    }

    public double calTotalIncomeGivingYear(int year) {
        LocalDateTime startOfYear =  LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("category").is("INCOME")
                        .andOperator(
                                Criteria.where("transactionDate").gte(startOfYear.atZone(ZoneId.systemDefault()).toInstant()),
                                Criteria.where("transactionDate").lte(endOfYear.atZone(ZoneId.systemDefault()).toInstant())
                        )
                ),
                Aggregation.group().sum("amount").as("totalIncome")
        );

        List<Map> results = mongoTemplate.aggregate(aggregation, Transaction.class, Map.class).getMappedResults();

        if(!results.isEmpty()){
            Map result = results.get(0);
            return (double) result.get("totalIncome");
        }
        return 0.0;
    }

    public double getBudgetIncomeAmountByYear(int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        Date start = Date.from(startOfYear.atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endOfYear.atZone(ZoneId.systemDefault()).toInstant());

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("budgetCategory").is("INCOME")
                        .andOperator(
                                Criteria.where("startDate").lte(end),
                                Criteria.where("endDate").gte(start)
                        )
                ),
                Aggregation.group().sum("amount").as("totalIncome")
        );


        List<Map> results = mongoTemplate.aggregate(aggregation, "budget", Map.class).getMappedResults();

        if (!results.isEmpty()) {
            Map result = results.get(0);
            return result.get("totalIncome") != null ? ((Number) result.get("totalIncome")).doubleValue() : 0.0;
        }
        return 0.0;
    }

    public double calTotalExpense() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("category").is("EXPENSE")),
                Aggregation.group().sum("amount").as("totalIncome")
        );

        List<Map> results = mongoTemplate.aggregate(aggregation, Transaction.class, Map.class).getMappedResults();

        if (!results.isEmpty()) {
            Map result = results.get(0);
            return (double) result.get("totalIncome");
        }
        return 0.0;
    }

    public double calTotalExpenseGivingYear(int year) {
        LocalDateTime startOfYear =  LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("category").is("EXPENSE")
                        .andOperator(
                                Criteria.where("transactionDate").gte(startOfYear.atZone(ZoneId.systemDefault()).toInstant()),
                                Criteria.where("transactionDate").lte(endOfYear.atZone(ZoneId.systemDefault()).toInstant())
                        )
                ),
                Aggregation.group().sum("amount").as("totalIncome")
        );

        List<Map> results = mongoTemplate.aggregate(aggregation, Transaction.class, Map.class).getMappedResults();

        if(!results.isEmpty()){
            Map result = results.get(0);
            return (double) result.get("totalIncome");
        }
        return 0.0;
    }

    public double getBudgetExpenseAmountByYear(int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        Date start = Date.from(startOfYear.atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endOfYear.atZone(ZoneId.systemDefault()).toInstant());

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("budgetCategory").is("EXPENSE")
                        .andOperator(
                                Criteria.where("startDate").lte(end),
                                Criteria.where("endDate").gte(start)
                        )
                ),
                Aggregation.group().sum("amount").as("totalExpense")
        );


        List<Map> results = mongoTemplate.aggregate(aggregation, "budget", Map.class).getMappedResults();

        if (!results.isEmpty()) {
            Map result = results.get(0);
            return result.get("totalExpense") != null ? ((Number) result.get("totalExpense")).doubleValue() : 0.0;
        }
        return 0.0;
    }

    public Map<String, Double> getSpendingTrends(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);

        return transactions.stream()
                .filter(transaction -> transaction.getCategory().toString().equalsIgnoreCase("EXPENSE"))
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().toLocalDate().toString(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public Map<Category, Double> getIncomeVsExpenses(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public List<Transaction> filterTransactions(String userId, String category, String tag, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate).stream()
                .filter(transaction -> (category == null || transaction.getCategory().toString().equalsIgnoreCase(category)) &&
                        (tag == null || transaction.getTag().toString().equalsIgnoreCase(tag)))
                .collect(Collectors.toList());
    }

    public byte[] readFileAsBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.readAllBytes(file.toPath());
    }
}
