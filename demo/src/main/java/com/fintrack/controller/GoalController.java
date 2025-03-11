package com.fintrack.controller;

import com.fintrack.dto.GoalDto;
import com.fintrack.entity.Goal;
import com.fintrack.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @GetMapping("/{id}/monthly-progress")
    public long getMonthlyGoalProgress(@PathVariable String id) {
        return goalService.getMonthlyGoalProgress(id);
    }

    @PostMapping
    public ResponseEntity<String> createGoal(@RequestBody GoalDto goalDto) {
        goalService.createGoal(
                goalDto.getUserId(),
                goalDto.getGoalName(),
                goalDto.getTargetAmount(),
                goalDto.getCurrentAmount(),
                goalDto.getDueDate(),
                goalDto.getSavingsRate()
        );
        return ResponseEntity.ok("Goal created successfully");
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        List<Goal> goals = goalService.getAllGoals();
        return goals.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String id) {
        Optional<Goal> goal = goalService.getGoalById(id);
        return goal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateGoal(@PathVariable String id, @RequestBody GoalDto goalDto) {
        Optional<Goal> updateGoal = goalService.updateGoal(
                id,
                goalDto.getUserId(),
                goalDto.getGoalName(),
                goalDto.getTargetAmount(),
                goalDto.getCurrentAmount(),
                goalDto.getDueDate(),
                goalDto.getSavingsRate()
        );
        return updateGoal.map(goal -> ResponseEntity.ok("Goal updated successfully"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGoal(@PathVariable String id) {
        if(goalService.deleteGoal(id)) {
            return ResponseEntity.ok("Goal deleted successfully");
        }else{
            return ResponseEntity.notFound().build();
        }
    }

}
