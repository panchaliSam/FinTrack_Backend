package com.fintrack.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "goals")
public class Goal {

    @Id
    private String goalId;
    private String userId;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private LocalDateTime dueDate;
    private double savingsRate;

}
