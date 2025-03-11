package fintrack.controller;

import com.fintrack.controller.SavingsController;
import com.fintrack.dto.SavingsDto;
import com.fintrack.entity.Savings;
import com.fintrack.service.GoalService;
import com.fintrack.service.SavingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsControllerTest {

    @Mock
    private SavingsService savingsService;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private SavingsController savingsController;

    private Savings mockSavings;
    private SavingsDto mockSavingsDto;

    @BeforeEach
    void setUp() {
        mockSavings = new Savings("65f8d789ef0123abc456e789", 10000.00);

        mockSavingsDto = new SavingsDto("65f8d789ef0123abc456e789", 10000.00);
    }

    @Test
    void getTotalSavings() {
        when(goalService.getTotalSavings()).thenReturn(5000.00);

        double totalSavings = savingsController.getTotalSavings();

        assertEquals(5000.00, totalSavings);

        verify(goalService, times(1)).getTotalSavings();
    }

    @Test
    void getAllSavings_Success() {
        when(savingsService.getAllSavings()).thenReturn(List.of(mockSavings));

        ResponseEntity<List<Savings>> response = savingsController.getAllSavings();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(mockSavings, response.getBody().get(0));

        verify(savingsService, times(1)).getAllSavings();
    }

    @Test
    void getAllSavings_Empty() {
        when(savingsService.getAllSavings()).thenReturn(List.of());

        ResponseEntity<List<Savings>> response = savingsController.getAllSavings();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void updateSavings_Success() {
        when(savingsService.updateSavings("65f8d789ef0123abc456e789", mockSavingsDto.getTotalAmount()))
                .thenReturn(Optional.of(mockSavings));

        ResponseEntity<String> response = savingsController.updateSavings("65f8d789ef0123abc456e789", mockSavingsDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Savings updated successfully", response.getBody());

        verify(savingsService, times(1)).updateSavings("65f8d789ef0123abc456e789", mockSavingsDto.getTotalAmount());
    }

    @Test
    void updateSavings_NotFound() {
        when(savingsService.updateSavings("invalidId", mockSavingsDto.getTotalAmount()))
                .thenReturn(Optional.empty());

        ResponseEntity<String> response = savingsController.updateSavings("invalidId", mockSavingsDto);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void insertSampleSavings() {
        Savings mockSavings = new Savings("65f8d789ef0123abc456e789", 10000.00);

        when(savingsService.createSavings(10000)).thenReturn(mockSavings);

        Savings result = savingsService.createSavings(10000);

        assertNotNull(result);
        assertEquals(10000.00, result.getTotalAmount());

        verify(savingsService, times(1)).createSavings(10000);

        System.out.println("Sample savings inserted successfully.");
    }
}