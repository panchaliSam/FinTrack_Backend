package fintrack.controller;

import com.fintrack.controller.TransactionController;
import com.fintrack.dto.TransactionDto;
import com.fintrack.entity.RecurrencePattern;
import com.fintrack.entity.Transaction;
import com.fintrack.service.TransactionService;
import com.fintrack.type.Category;
import com.fintrack.type.RecurrenceFrequency;
import com.fintrack.type.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction mockTransaction;
    private TransactionDto mockTransactionDto;
    private RecurrencePattern mockRecurrencePattern;

    @BeforeEach
    void setUp() {
        // Create a mock RecurrencePattern for recurring transactions
        mockRecurrencePattern = new RecurrencePattern(
                RecurrenceFrequency.MONTHLY,
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(6)
        );

//        mockTransaction = new Transaction(
//                "67b853e12101d21c20fad9ee",
//                Tag.BUSINESS,
//                Category.INCOME,
//                "This is a sample transaction",
//                200.00,
//                LocalDateTime.now(),
//                false
//        );

        mockTransactionDto = new TransactionDto(
                "67b853e12101d21c20fad9ee",
                Tag.BUSINESS,
                Category.INCOME,
                "This is a sample transaction",
                200.00,
                LocalDateTime.now(),
                true,
                RecurrenceFrequency.DAILY
        );
    }

    @Test
    void createTransaction() {
        // Simulating the behavior of service returning a newly created transaction
        when(transactionService.createTransaction(
                mockTransactionDto.getUserId(),
                mockTransactionDto.getTag(),
                mockTransactionDto.getCategory(),
                mockTransactionDto.getDescription(),
                mockTransactionDto.getAmount(),
                mockTransactionDto.getTransactionDate(),
                mockTransactionDto.isRecurring(),
                mockTransactionDto.getRecurrenceFrequency()
        )).thenReturn(mockTransaction);

        ResponseEntity<String> response = transactionController.createTransaction(mockTransactionDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transaction created successfully", response.getBody());

        verify(transactionService, times(1)).createTransaction(
                mockTransactionDto.getUserId(),
                mockTransactionDto.getTag(),
                mockTransactionDto.getCategory(),
                mockTransactionDto.getDescription(),
                mockTransactionDto.getAmount(),
                mockTransactionDto.getTransactionDate(),
                mockTransactionDto.isRecurring(),
                mockTransactionDto.getRecurrenceFrequency()
        );
    }

    @Test
    void getAllTransactions_Success() {
        when(transactionService.getAllTransactions()).thenReturn(List.of(mockTransaction));

        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(mockTransaction, response.getBody().get(0));

        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    void getAllTransactions_Empty() {
        when(transactionService.getAllTransactions()).thenReturn(List.of());

        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getTransactionById_Found() {
        when(transactionService.getTransactionById("65f8c321abc456d789e01234")).thenReturn(Optional.of(mockTransaction));

        ResponseEntity<Transaction> response = transactionController.getTransactionById("65f8c321abc456d789e01234");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTransaction, response.getBody());
    }

    @Test
    void getTransactionById_NotFound() {
        when(transactionService.getTransactionById("invalidId")).thenReturn(Optional.empty());

        ResponseEntity<Transaction> response = transactionController.getTransactionById("invalidId");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void updateTransaction_Success() {
        when(transactionService.updateTransaction(
                "65f8c321abc456d789e01234",
                mockTransactionDto.getUserId(),
                mockTransactionDto.getTag(),
                mockTransactionDto.getCategory(),
                mockTransactionDto.getDescription(),
                mockTransactionDto.getAmount(),
                mockTransactionDto.getTransactionDate(),
                mockTransactionDto.isRecurring(),
                mockTransactionDto.getRecurrenceFrequency()
        )).thenReturn(Optional.of(mockTransaction));

        ResponseEntity<String> response = transactionController.updateTransaction("65f8c321abc456d789e01234", mockTransactionDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transaction updated successfully", response.getBody());

        verify(transactionService, times(1)).updateTransaction(
                "65f8c321abc456d789e01234",
                mockTransactionDto.getUserId(),
                mockTransactionDto.getTag(),
                mockTransactionDto.getCategory(),
                mockTransactionDto.getDescription(),
                mockTransactionDto.getAmount(),
                mockTransactionDto.getTransactionDate(),
                mockTransactionDto.isRecurring(),
                mockTransactionDto.getRecurrenceFrequency()
        );
    }

    @Test
    void updateTransaction_NotFound() {
        when(transactionService.updateTransaction(
                "invalidId",
                mockTransactionDto.getUserId(),
                mockTransactionDto.getTag(),
                mockTransactionDto.getCategory(),
                mockTransactionDto.getDescription(),
                mockTransactionDto.getAmount(),
                mockTransactionDto.getTransactionDate(),
                mockTransactionDto.isRecurring(),
                mockTransactionDto.getRecurrenceFrequency()
        )).thenReturn(Optional.empty());

        ResponseEntity<String> response = transactionController.updateTransaction("invalidId", mockTransactionDto);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void deleteTransaction_Success() {
        when(transactionService.deleteTransaction("65f8c321abc456d789e01234")).thenReturn(true);

        ResponseEntity<String> response = transactionController.deleteTransaction("65f8c321abc456d789e01234");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transaction deleted successfully", response.getBody());
    }

    @Test
    void deleteTransaction_NotFound() {
        when(transactionService.deleteTransaction("invalidId")).thenReturn(false);

        ResponseEntity<String> response = transactionController.deleteTransaction("invalidId");

        assertEquals(404, response.getStatusCodeValue());
    }
}