package fintrack.service;

import com.fintrack.entity.Transaction;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.service.TransactionService;
import com.fintrack.type.Category;
import com.fintrack.type.RecurrenceFrequency;
import com.fintrack.type.Tag;
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
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction mockTransaction;
    private ObjectId transactionId;
    private ObjectId userId;

    @BeforeEach
    void setUp() {
        transactionId = new ObjectId();
        userId = new ObjectId();

        // Adjust the recurrence to use RecurrenceFrequency directly
        mockTransaction = new Transaction();
        mockTransaction.setTransactionId(transactionId.toString());
        mockTransaction.setUserId(userId.toString());
        mockTransaction.setTag(Tag.BUSINESS);
        mockTransaction.setCategory(Category.EXPENSE);
        mockTransaction.setDescription("Groceries shopping");
        mockTransaction.setAmount(150.50);
        mockTransaction.setTransactionDate(LocalDateTime.now());
        mockTransaction.setRecurring(true);
        mockTransaction.setRecurrenceFrequency(RecurrenceFrequency.MONTHLY);
    }

    @Test
    void createTransaction_Success() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        Transaction createdTransaction = transactionService.createTransaction(
                mockTransaction.getUserId(),
                mockTransaction.getTag(),
                mockTransaction.getCategory(),
                mockTransaction.getDescription(),
                mockTransaction.getAmount(),
                mockTransaction.getTransactionDate(),
                mockTransaction.isRecurring(),
                mockTransaction.getRecurrenceFrequency()
        );

        assertNotNull(createdTransaction);
        assertEquals(mockTransaction.getDescription(), createdTransaction.getDescription());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getAllTransactions_Success() {
        when(transactionRepository.findAll()).thenReturn(List.of(mockTransaction));

        List<Transaction> transactions = transactionService.getAllTransactions();

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
    }

    @Test
    void getAllTransactions_Empty() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        List<Transaction> transactions = transactionService.getAllTransactions();

        assertTrue(transactions.isEmpty());
    }

    @Test
    void getTransactionById_Found() {
        when(transactionRepository.findById(transactionId.toString())).thenReturn(Optional.of(mockTransaction));

        Optional<Transaction> transaction = transactionService.getTransactionById(transactionId.toString());

        assertTrue(transaction.isPresent());
        assertEquals(mockTransaction.getAmount(), transaction.get().getAmount());
    }

    @Test
    void getTransactionById_NotFound() {
        when(transactionRepository.findById("nonexistentId")).thenReturn(Optional.empty());

        Optional<Transaction> transaction = transactionService.getTransactionById("nonexistentId");

        assertFalse(transaction.isPresent());
    }

    @Test
    void getTransactionByUserId_Found() {
        when(transactionRepository.findByUserId(userId.toString())).thenReturn(List.of(mockTransaction));

        List<Transaction> transactions = transactionService.getTransactionByUserId(userId.toString());

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
    }

    @Test
    void getTransactionByUserId_NotFound() {
        when(transactionRepository.findByUserId("nonexistentUserId")).thenReturn(List.of());

        List<Transaction> transactions = transactionService.getTransactionByUserId("nonexistentUserId");

        assertTrue(transactions.isEmpty());
    }

    @Test
    void updateTransaction_Success() {
        when(transactionRepository.findById(transactionId.toString())).thenReturn(Optional.of(mockTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        Optional<Transaction> updatedTransaction = transactionService.updateTransaction(
                transactionId.toString(),
                userId.toString(),
                Tag.BUSINESS, // Changing Tag
                Category.INCOME, // Changing Category
                "Salary Deposit", // Changing Description
                2000.00, // Changing Amount
                mockTransaction.getTransactionDate(),
                false, // Changing Recurring status
                null
        );

        assertTrue(updatedTransaction.isPresent());
        assertEquals(2000.00, updatedTransaction.get().getAmount());
        assertEquals("Salary Deposit", updatedTransaction.get().getDescription());
        assertEquals(Category.INCOME, updatedTransaction.get().getCategory());
    }

    @Test
    void updateTransaction_NotFound() {
        when(transactionRepository.findById("nonexistentId")).thenReturn(Optional.empty());

        Optional<Transaction> updatedTransaction = transactionService.updateTransaction(
                "nonexistentId",
                userId.toString(),
                mockTransaction.getTag(),
                mockTransaction.getCategory(),
                mockTransaction.getDescription(),
                mockTransaction.getAmount(),
                mockTransaction.getTransactionDate(),
                mockTransaction.isRecurring(),
                mockTransaction.getRecurrenceFrequency()
        );

        assertFalse(updatedTransaction.isPresent());
    }

    @Test
    void deleteTransaction_Success() {
        when(transactionRepository.findById(transactionId.toString())).thenReturn(Optional.of(mockTransaction));
        doNothing().when(transactionRepository).deleteById(transactionId.toString());

        boolean result = transactionService.deleteTransaction(transactionId.toString());

        assertTrue(result);
        verify(transactionRepository, times(1)).deleteById(transactionId.toString());
    }

    @Test
    void deleteTransaction_NotFound() {
        when(transactionRepository.findById("nonexistentId")).thenReturn(Optional.empty());

        boolean result = transactionService.deleteTransaction("nonexistentId");

        assertFalse(result);
    }
}
