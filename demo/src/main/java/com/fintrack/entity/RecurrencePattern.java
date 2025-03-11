package com.fintrack.entity;

import com.fintrack.type.RecurrenceFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecurrencePattern {
    private RecurrenceFrequency frequency;
    private LocalDateTime nextDate;
    private LocalDateTime endDate;
}
