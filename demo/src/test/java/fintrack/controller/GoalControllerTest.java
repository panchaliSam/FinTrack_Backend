package fintrack.controller;

import com.fintrack.controller.GoalController;
import com.fintrack.dto.GoalDto;
import com.fintrack.entity.Goal;
import com.fintrack.service.GoalService;
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
class GoalControllerTest {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private Goal mockGoal;
    private GoalDto mockGoalDto;

    @BeforeEach
    void setUp() {
        mockGoal = new Goal("65f8e789ef0123abc456e890", "67cd0d8aa332965005d0de62",
                "Buying a car", 2000, 0, LocalDateTime.of(2025, 12, 1, 0, 0), 0.1);

        mockGoalDto = new GoalDto("67cd0d8aa332965005d0de62", "Buying a car",
                2000, 0, LocalDateTime.of(2025, 12, 1, 0, 0), 0.1);
    }

    @Test
    void getMonthlyGoalProgress() {
        when(goalService.getMonthlyGoalProgress("65f8e789ef0123abc456e890")).thenReturn(100L);

        long progress = goalController.getMonthlyGoalProgress("65f8e789ef0123abc456e890");

        assertEquals(100, progress);

        verify(goalService, times(1)).getMonthlyGoalProgress("65f8e789ef0123abc456e890");
    }

    @Test
    void createGoal() {
        when(goalService.createGoal(
                mockGoalDto.getUserId(),
                mockGoalDto.getGoalName(),
                mockGoalDto.getTargetAmount(),
                mockGoalDto.getCurrentAmount(),
                mockGoalDto.getDueDate(),
                mockGoalDto.getSavingsRate()
        )).thenReturn(mockGoal);

        ResponseEntity<String> response = goalController.createGoal(mockGoalDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Goal created successfully", response.getBody());

        verify(goalService, times(1)).createGoal(
                mockGoalDto.getUserId(),
                mockGoalDto.getGoalName(),
                mockGoalDto.getTargetAmount(),
                mockGoalDto.getCurrentAmount(),
                mockGoalDto.getDueDate(),
                mockGoalDto.getSavingsRate()
        );
    }

    @Test
    void getAllGoals_Success() {
        when(goalService.getAllGoals()).thenReturn(List.of(mockGoal));

        ResponseEntity<List<Goal>> response = goalController.getAllGoals();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(mockGoal, response.getBody().get(0));

        verify(goalService, times(1)).getAllGoals();
    }

    @Test
    void getAllGoals_Empty() {
        when(goalService.getAllGoals()).thenReturn(List.of());

        ResponseEntity<List<Goal>> response = goalController.getAllGoals();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getGoalById_Found() {
        when(goalService.getGoalById("65f8e789ef0123abc456e890")).thenReturn(Optional.of(mockGoal));

        ResponseEntity<Goal> response = goalController.getGoalById("65f8e789ef0123abc456e890");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockGoal, response.getBody());
    }

    @Test
    void getGoalById_NotFound() {
        when(goalService.getGoalById("invalidId")).thenReturn(Optional.empty());

        ResponseEntity<Goal> response = goalController.getGoalById("invalidId");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void updateGoal_Success() {
        when(goalService.updateGoal(
                "65f8e789ef0123abc456e890",
                mockGoalDto.getUserId(),
                mockGoalDto.getGoalName(),
                mockGoalDto.getTargetAmount(),
                mockGoalDto.getCurrentAmount(),
                mockGoalDto.getDueDate(),
                mockGoalDto.getSavingsRate()
        )).thenReturn(Optional.of(mockGoal));

        ResponseEntity<String> response = goalController.updateGoal("65f8e789ef0123abc456e890", mockGoalDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Goal updated successfully", response.getBody());

        verify(goalService, times(1)).updateGoal(
                "65f8e789ef0123abc456e890",
                mockGoalDto.getUserId(),
                mockGoalDto.getGoalName(),
                mockGoalDto.getTargetAmount(),
                mockGoalDto.getCurrentAmount(),
                mockGoalDto.getDueDate(),
                mockGoalDto.getSavingsRate()
        );
    }

    @Test
    void updateGoal_NotFound() {
        when(goalService.updateGoal(
                "invalidId",
                mockGoalDto.getUserId(),
                mockGoalDto.getGoalName(),
                mockGoalDto.getTargetAmount(),
                mockGoalDto.getCurrentAmount(),
                mockGoalDto.getDueDate(),
                mockGoalDto.getSavingsRate()
        )).thenReturn(Optional.empty());

        ResponseEntity<String> response = goalController.updateGoal("invalidId", mockGoalDto);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void deleteGoal_Success() {
        when(goalService.deleteGoal("65f8e789ef0123abc456e890")).thenReturn(true);

        ResponseEntity<String> response = goalController.deleteGoal("65f8e789ef0123abc456e890");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Goal deleted successfully", response.getBody());
    }

    @Test
    void deleteGoal_NotFound() {
        when(goalService.deleteGoal("invalidId")).thenReturn(false);

        ResponseEntity<String> response = goalController.deleteGoal("invalidId");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void insertSampleGoal() {
        when(goalService.createGoal(
                "67cd0d8aa332965005d0de62",
                "Buying a car",
                2000,
                0,
                LocalDateTime.of(2025, 12, 1, 0, 0),
                0.1
        )).thenReturn(mockGoal);

        Goal result = goalService.createGoal(
                "67cd0d8aa332965005d0de62",
                "Buying a car",
                2000,
                0,
                LocalDateTime.of(2025, 12, 1, 0, 0),
                0.1
        );

        assertNotNull(result);
        assertEquals("Buying a car", result.getGoalName());

        verify(goalService, times(1)).createGoal(
                "67cd0d8aa332965005d0de62",
                "Buying a car",
                2000,
                0,
                LocalDateTime.of(2025, 12, 1, 0, 0),
                0.1
        );

        System.out.println("Sample financial goal inserted successfully.");
    }
}