package fintrack.repository;

import com.fintrack.entity.Goal;
import com.fintrack.repository.GoalRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalRepositoryTest {

    @Mock
    private GoalRepository goalRepository;

    private Goal goal;
    private String goalId;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = new ObjectId().toString();
        goalId = new ObjectId().toString();

        goal = new Goal();
        goal.setGoalId(goalId);
        goal.setUserId(userId);
        goal.setGoalName("Save for Car");
        goal.setTargetAmount(10000.00);
    }

    @Test
    void findUserEmailByGoalId_Success() {
        String mockEmail = "testuser@example.com";

        when(goalRepository.findUserEmailByGoalId(goalId)).thenReturn(mockEmail);

        String retrievedEmail = goalRepository.findUserEmailByGoalId(goalId);

        assertNotNull(retrievedEmail);
        assertEquals(mockEmail, retrievedEmail);

        verify(goalRepository, times(1)).findUserEmailByGoalId(goalId);
    }

    @Test
    void findById_Success() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        Optional<Goal> retrievedGoal = goalRepository.findById(goalId);

        assertTrue(retrievedGoal.isPresent());
        assertEquals(goalId, retrievedGoal.get().getGoalId());
        assertEquals(userId, retrievedGoal.get().getUserId());
        assertEquals("Save for Car", retrievedGoal.get().getGoalName());
        assertEquals(10000.00, retrievedGoal.get().getTargetAmount());

        verify(goalRepository, times(1)).findById(goalId);
    }

    @Test
    void findById_NotFound() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        Optional<Goal> retrievedGoal = goalRepository.findById(goalId);

        assertFalse(retrievedGoal.isPresent());

        verify(goalRepository, times(1)).findById(goalId);
    }
}