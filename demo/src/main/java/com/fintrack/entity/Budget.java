package com.fintrack.entity;

import com.fintrack.type.BudgetStatus;
import com.fintrack.type.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "budget")
public class Budget {
    @Id
    private String budgetId;
    private String userId;
    private Category budgetCategory;
    private double amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BudgetStatus budgetStatus;

}
