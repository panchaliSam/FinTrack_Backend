package fintrack.repository;

import com.fintrack.entity.Transaction;
import com.fintrack.repository.TransactionRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryTest {

    @Mock
    private TransactionRepository transactionRepository;

    private Transaction transaction;
    private String transactionId;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = new ObjectId().toString();
        transactionId = new ObjectId().toString();

        transaction = new Transaction();
        transaction.setTransactionId(String.valueOf(new ObjectId(transactionId)));
        transaction.setUserId(userId);
        transaction.setAmount(500.00);
        transaction.setTransactionDate(LocalDateTime.now().minusDays(1));
        transaction.setDescription("Grocery Shopping");
    }

    @Test
    void findByUserId_Success() {
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));

        List<Transaction> retrievedTransactions = transactionRepository.findByUserId(userId);

        assertNotNull(retrievedTransactions);
        assertEquals(1, retrievedTransactions.size());
        assertEquals(userId, retrievedTransactions.get(0).getUserId());
        assertEquals(500.00, retrievedTransactions.get(0).getAmount());
        assertEquals("Grocery Shopping", retrievedTransactions.get(0).getDescription());

        verify(transactionRepository, times(1)).findByUserId(userId);
    }

    @Test
    void findByUserIdAndTransactionDateBetween_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();

        when(transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate))
                .thenReturn(List.of(transaction));

        List<Transaction> retrievedTransactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);

        assertNotNull(retrievedTransactions);
        assertEquals(1, retrievedTransactions.size());
        assertEquals(userId, retrievedTransactions.get(0).getUserId());
        assertEquals(500.00, retrievedTransactions.get(0).getAmount());
        assertEquals("Grocery Shopping", retrievedTransactions.get(0).getDescription());

        verify(transactionRepository, times(1)).findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
    }

    @Test
    void findById_Success() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Optional<Transaction> retrievedTransaction = transactionRepository.findById(transactionId);

        assertTrue(retrievedTransaction.isPresent());
        assertEquals(transactionId, retrievedTransaction.get().getTransactionId().toString());
        assertEquals(userId, retrievedTransaction.get().getUserId());
        assertEquals(500.00, retrievedTransaction.get().getAmount());

        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void findById_NotFound() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        Optional<Transaction> retrievedTransaction = transactionRepository.findById(transactionId);

        assertFalse(retrievedTransaction.isPresent());

        verify(transactionRepository, times(1)).findById(transactionId);
    }
}