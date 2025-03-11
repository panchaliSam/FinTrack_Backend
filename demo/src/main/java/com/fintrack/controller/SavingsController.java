package com.fintrack.controller;

import com.fintrack.dto.SavingsDto;
import com.fintrack.entity.Savings;
import com.fintrack.service.GoalService;
import com.fintrack.service.SavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/savings")
public class SavingsController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private SavingsService savingsService;

    @GetMapping("/total-savings")
    public double getTotalSavings() {
        return goalService.getTotalSavings();
    }

    @GetMapping
    public ResponseEntity<List<Savings>> getAllSavings() {
        List<Savings> savings = savingsService.getAllSavings();
        return savings.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(savings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSavings(@PathVariable String id, @RequestBody SavingsDto savingsDto) {
        Optional<Savings> updateSavings = savingsService.updateSavings(
                id,
                savingsDto.getTotalAmount()
        );

        return updateSavings
                .map(savings -> ResponseEntity.ok("Savings updated successfully"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
