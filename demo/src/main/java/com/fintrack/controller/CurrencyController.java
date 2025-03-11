package com.fintrack.controller;

import com.fintrack.service.CurrencyService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/convert")
    public BigDecimal convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {
        return currencyService.convert(from, to, amount);
    }
}