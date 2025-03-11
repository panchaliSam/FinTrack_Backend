package fintrack.controller;//package com.fintrack.controller;

import com.fintrack.FinTrackApplication;
import com.fintrack.dto.TransactionDto;
import com.fintrack.entity.Transaction;
import com.fintrack.service.TransactionService;
import com.fintrack.type.Category;
import com.fintrack.type.Tag;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FinTrackApplication.class
)
@Import(TransactionControllerIntegrationTest.TestConfig.class)
public class TransactionControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionService transactionService;

    private Transaction mockTransaction;
    private String baseUrl;
    private String mockTransactionId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/transaction";
        mockTransactionId = new ObjectId().toHexString();

        mockTransaction = new Transaction(
                mockTransactionId,
                new ObjectId().toHexString(),
                Tag.BUSINESS,
                Category.INCOME,
                "Monthly salary",
                5000.0,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                false,
                null
        );

        Mockito.when(transactionService.getAllTransactions()).thenReturn(List.of(mockTransaction));
        Mockito.when(transactionService.getTransactionById(mockTransactionId)).thenReturn(Optional.of(mockTransaction));
        Mockito.when(transactionService.createTransaction(any(), any(), any(), any(), anyDouble(), any(), anyBoolean(), any()))
                .thenReturn(mockTransaction);
        Mockito.when(transactionService.updateTransaction(eq(mockTransactionId), any(), any(), any(), any(), anyDouble(), any(), anyBoolean(), any()))
                .thenReturn(Optional.of(mockTransaction));
        Mockito.when(transactionService.deleteTransaction(mockTransactionId)).thenReturn(true);
    }

    @Test
    void testGetAllTransactions() {
        ResponseEntity<Transaction[]> response = restTemplate.getForEntity(baseUrl, Transaction[].class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(Tag.BUSINESS, response.getBody()[0].getTag());
    }

    @Test
    void testGetTransactionById() {
        ResponseEntity<Transaction> response = restTemplate.getForEntity(baseUrl + "/" + mockTransactionId, Transaction.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(Tag.BUSINESS, response.getBody().getTag());
    }

    @Test
    void testCreateTransaction() {
        TransactionDto newTransaction = new TransactionDto(
                new ObjectId().toHexString(),
                Tag.BUSINESS,
                Category.INCOME,
                "Annual bonus",
                1000.0,
                LocalDateTime.of(2025, 3, 1, 0, 0),
                false,
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TransactionDto> request = new HttpEntity<>(newTransaction, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Transaction created successfully", response.getBody());
    }

    @Test
    void testUpdateTransaction() {
        TransactionDto updatedTransaction = new TransactionDto(
                new ObjectId().toHexString(),
                Tag.BUSINESS,
                Category.INCOME,
                "Freelance project payment",
                1200.0,
                LocalDateTime.of(2025, 4, 1, 0, 0),
                false,
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TransactionDto> request = new HttpEntity<>(updatedTransaction, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + mockTransactionId, HttpMethod.PUT, request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Transaction updated successfully", response.getBody());
    }

    @Test
    void testDeleteTransaction() {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + mockTransactionId, HttpMethod.DELETE, null, String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteTransactionNotFound() {
        String invalidId = new ObjectId().toHexString();
        when(transactionService.deleteTransaction(invalidId)).thenReturn(false);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + invalidId, HttpMethod.DELETE, null, String.class);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public TransactionService transactionService() {
            return mock(TransactionService.class);
        }
    }
}