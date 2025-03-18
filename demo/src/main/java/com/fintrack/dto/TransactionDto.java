package com.fintrack.dto;

import com.fintrack.type.Category;
import com.fintrack.type.Tag;
import com.fintrack.type.RecurrenceFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String userId;
    private Tag tag;
    private Category category;
    private String description;
    private double amount;
    private LocalDateTime transactionDate;
    private boolean isRecurring;
    private RecurrenceFrequency recurrenceFrequency;
}