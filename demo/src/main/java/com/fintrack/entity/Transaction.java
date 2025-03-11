package com.fintrack.entity;

import com.fintrack.type.Category;
import com.fintrack.type.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transaction")
public class Transaction {

    @Id
    private String transactionId;
    private String userId;
    private Tag tag;
    private Category category;
    private String description;
    private double amount;
    private LocalDateTime transactionDate;
    private boolean isRecurring;
    private RecurrencePattern recurrencePattern;
}
