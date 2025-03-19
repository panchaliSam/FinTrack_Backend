package com.fintrack.entity;

import com.fintrack.type.Category;
import com.fintrack.type.Tag;
import com.fintrack.type.RecurrenceFrequency;
import jakarta.persistence.Id; // MongoDB uses @Id to define the primary key
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
    private RecurrenceFrequency recurrenceFrequency;
    private LocalDateTime nextRecurrenceDate;

    @DBRef
    private User user;

}