package com.fintrack.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.api.key}")
    private String apiKey;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal convert(String from, String to, BigDecimal amount) {
        String url = apiUrl + "?access_key=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);

        JSONObject jsonResponse = new JSONObject(response);
        JSONObject rates = jsonResponse.getJSONObject("rates");

        if (!rates.has(from) || !rates.has(to)) {
            throw new RuntimeException("Invalid currency code: " + from + " or " + to);
        }

        BigDecimal fromRate = rates.getBigDecimal(from);
        BigDecimal toRate = rates.getBigDecimal(to);

        return amount.multiply(toRate).divide(fromRate, 4, BigDecimal.ROUND_HALF_UP);
    }
}