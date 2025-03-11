package fintrack.service;

import com.fintrack.entity.Savings;
import com.fintrack.repository.SavingsRepository;
import com.fintrack.service.SavingsService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsServiceTest {

    @Mock
    private SavingsRepository savingsRepository;

    @InjectMocks
    private SavingsService savingsService;

    private Savings mockSavings;
    private ObjectId savingsId;

    @BeforeEach
    void setUp() {
        savingsId = new ObjectId();

        mockSavings = new Savings();
        mockSavings.setSavingsId(savingsId.toString());
        mockSavings.setTotalAmount(10000);
    }

    @Test
    void createSavings_Success() {
        when(savingsRepository.save(any(Savings.class))).thenReturn(mockSavings);

        Savings createdSavings = savingsService.createSavings(10000);

        assertNotNull(createdSavings);
        assertEquals(10000, createdSavings.getTotalAmount());
        verify(savingsRepository, times(1)).save(any(Savings.class));
    }

    @Test
    void getAllSavings_Success() {
        when(savingsRepository.findAll()).thenReturn(List.of(mockSavings));

        List<Savings> savingsList = savingsService.getAllSavings();

        assertFalse(savingsList.isEmpty());
        assertEquals(1, savingsList.size());
    }

    @Test
    void getAllSavings_EmptyList() {
        when(savingsRepository.findAll()).thenReturn(List.of());

        List<Savings> savingsList = savingsService.getAllSavings();

        assertTrue(savingsList.isEmpty());
    }

    @Test
    void updateSavings_Success() {
        when(savingsRepository.findById(savingsId.toString())).thenReturn(Optional.of(mockSavings));
        when(savingsRepository.save(any(Savings.class))).thenReturn(mockSavings);

        Optional<Savings> updatedSavings = savingsService.updateSavings(savingsId.toString(), 15000);

        assertTrue(updatedSavings.isPresent());
        assertEquals(15000, updatedSavings.get().getTotalAmount());
    }

    @Test
    void updateSavings_NotFound() {
        when(savingsRepository.findById("nonexistentId")).thenReturn(Optional.empty());

        Optional<Savings> updatedSavings = savingsService.updateSavings("nonexistentId", 15000);

        assertFalse(updatedSavings.isPresent());
    }

    @Test
    void deleteSavings_Success() {
        when(savingsRepository.findById(savingsId.toString())).thenReturn(Optional.of(mockSavings));
        doNothing().when(savingsRepository).delete(mockSavings);

        boolean result = savingsService.deleteSavings(savingsId.toString());

        assertTrue(result);
        verify(savingsRepository, times(1)).delete(mockSavings);
    }

    @Test
    void deleteSavings_NotFound() {
        when(savingsRepository.findById("nonexistentId")).thenReturn(Optional.empty());

        boolean result = savingsService.deleteSavings("nonexistentId");

        assertFalse(result);
    }
}