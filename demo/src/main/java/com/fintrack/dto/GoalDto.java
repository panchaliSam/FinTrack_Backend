package com.fintrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {
    private String userId;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private LocalDateTime dueDate;
    private double savingsRate;
}
