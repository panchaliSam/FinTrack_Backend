package com.fintrack.service;

import com.fintrack.entity.Savings;
import com.fintrack.repository.SavingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SavingsService {

    private final SavingsRepository savingsRepository;

    public SavingsService(SavingsRepository savingsRepository) {
        this.savingsRepository = savingsRepository;
    }

    public Savings createSavings(double totalAmount) {
        Savings savings = new Savings();
        savings.setTotalAmount(totalAmount);
        return savingsRepository.save(savings);
    }

    public List<Savings> getAllSavings() {return savingsRepository.findAll();}

    public Optional<Savings> updateSavings(String savingId, double totalAmount) {
        Optional<Savings> existingSavings = savingsRepository.findById(savingId);
        if(existingSavings.isPresent()) {
            Savings savings = existingSavings.get();
            savings.setTotalAmount(totalAmount);
            return Optional.of(savingsRepository.save(savings));
        }
        return Optional.empty();
    }

    public boolean deleteSavings(String savingId) {
        Optional<Savings> existingSavings = savingsRepository.findById(savingId);
        if(existingSavings.isPresent()) {
            savingsRepository.delete(existingSavings.get());
            return true;
        }
        return false;
    }

}
