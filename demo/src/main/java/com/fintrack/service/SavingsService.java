package com.fintrack.service;

import com.fintrack.entity.Savings;
import com.fintrack.repository.SavingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SavingsService {

    private final SavingsRepository savingsRepository;

    public SavingsService(SavingsRepository savingsRepository) {
        this.savingsRepository = savingsRepository;
    }

    public Savings createSavings(double totalAmount) {
        log.info("Creating savings with total amount: {}", totalAmount);
        Savings savings = new Savings();
        savings.setTotalAmount(totalAmount);
        Savings savedSavings = savingsRepository.save(savings);
        log.info("Savings created successfully with ID: {}", savedSavings.getSavingsId());
        return savedSavings;
    }

    public List<Savings> getAllSavings() {
        log.info("Fetching all savings.");
        return savingsRepository.findAll();
    }

    public Optional<Savings> updateSavings(String savingId, double totalAmount) {
        log.info("Updating savings with ID: {}", savingId);
        Optional<Savings> existingSavings = savingsRepository.findById(savingId);
        if(existingSavings.isPresent()) {
            Savings savings = existingSavings.get();
            savings.setTotalAmount(totalAmount);
            Savings updatedSavings = savingsRepository.save(savings);
            log.info("Savings with ID: {} updated successfully.", savingId);
            return Optional.of(updatedSavings);
        }
        log.error("Savings with ID: {} not found.", savingId);
        return Optional.empty();
    }

    public boolean deleteSavings(String savingId) {
        log.info("Attempting to delete savings with ID: {}", savingId);
        Optional<Savings> existingSavings = savingsRepository.findById(savingId);
        if(existingSavings.isPresent()) {
            savingsRepository.delete(existingSavings.get());
            log.info("Savings with ID: {} deleted successfully.", savingId);
            return true;
        }
        log.error("Savings with ID: {} not found.", savingId);
        return false;
    }
}
