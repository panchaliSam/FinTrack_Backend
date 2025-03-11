package com.fintrack.service;

import com.fintrack.entity.Goal;
import com.fintrack.entity.Savings;
import com.fintrack.repository.GoalRepository;
import com.fintrack.repository.SavingsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final SavingsRepository savingsRepository;
    private final EmailService emailService;

    public GoalService(GoalRepository goalRepository, SavingsRepository savingsRepository, EmailService emailService) {
        this.goalRepository = goalRepository;
        this.savingsRepository = savingsRepository;
        this.emailService = emailService;
    }

    public double getTotalSavings() {
        double totalAmount = 0.0;

        List<Savings> savingsList = savingsRepository.findAll();
        for (Savings savings : savingsList) {
            totalAmount += savings.getTotalAmount();
        }

        return totalAmount;
    }

    public double calculateMonthCount(Goal goal) {
        double totalAmount = getTotalSavings();
        double monthCount = 0.0;

        double savingsRate = goal.getSavingsRate();
        double targetAmount = goal.getTargetAmount();

        if (savingsRate > 0) {
            double monthlyAllocatedAmount = totalAmount / savingsRate;
            monthCount = targetAmount / monthlyAllocatedAmount;

            monthCount = (monthCount - Math.floor(monthCount) >= 0.5) ? Math.ceil(monthCount) : Math.floor(monthCount);
        }

        return monthCount;
    }

    public Map<String, Integer> getSavingsAchieveDate() {
        Map<String, Integer> monthToGoalMap = new HashMap<>();

        List<Goal> goalsList = goalRepository.findAll();

        for (Goal goal : goalsList) {
            double monthCount = calculateMonthCount(goal);

            List<String> months = new ArrayList<>();
            months.add("January");
            months.add("February");
            months.add("March");
            months.add("April");
            months.add("May");
            months.add("June");
            months.add("July");
            months.add("August");
            months.add("September");
            months.add("October");
            months.add("November");
            months.add("December");

            for (int i = 0; i < Math.min(monthCount, 12); i++) {
                String month = months.get(i);
                monthToGoalMap.put(month, monthToGoalMap.getOrDefault(month, 0) + 1);
            }
        }

        return monthToGoalMap;
    }

    public long getMonthlyGoalProgress(String goalId) {
        Optional<Goal> goal = goalRepository.findById(goalId);

        if (goal.isEmpty()) {
            throw new IllegalArgumentException("Goal not found for ID: " + goalId);
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDateLocal = goal.get().getDueDate().toLocalDate();

        return ChronoUnit.MONTHS.between(today.withDayOfMonth(1), dueDateLocal.withDayOfMonth(1));
    }

    public Goal createGoal(String userId, String goalName, double targetAmount, double currentAmount, LocalDateTime dueDate, double savingsRate) {
        Goal goal = new Goal();
        goal.setUserId(userId);
        goal.setGoalName(goalName);
        goal.setTargetAmount(targetAmount);
        goal.setCurrentAmount(currentAmount);
        goal.setDueDate(dueDate);
        goal.setSavingsRate(savingsRate);
        return goalRepository.save(goal);
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Optional<Goal> getGoalById(String goalId) {
        return goalRepository.findById(goalId);
    }

    public Optional<Goal> updateGoal(String goalId, String userId, String goalName, double targetAmount, double currentAmount, LocalDateTime dueDate, double savingsRate) {
        Optional<Goal> existingGoals = goalRepository.findById(goalId);
        if (existingGoals.isPresent()) {
            Goal goal = existingGoals.get();
            goal.setGoalId(userId);
            goal.setGoalName(goalName);
            goal.setTargetAmount(targetAmount);
            goal.setCurrentAmount(currentAmount);
            goal.setDueDate(dueDate);
            goal.setSavingsRate(savingsRate);
            return Optional.of(goalRepository.save(goal));
        }
        return Optional.empty();
    }

    public boolean deleteGoal(String goalId) {
        Optional<Goal> existingGoals = goalRepository.findById(goalId);
        if (existingGoals.isPresent()) {
            goalRepository.delete(existingGoals.get());
            return true;
        }
        return false;
    }

    @Scheduled(cron = "0 0 8 1 * ?")
    public void sendMonthlyNotifications() {
        goalRepository.findAll().forEach(goal -> {
            String email = goalRepository.findUserEmailByGoalId(goal.getGoalId());
            if (email != null && !email.isEmpty()) {
                long monthsLeft = getMonthlyGoalProgress(goal.getGoalId());
                String subject = "Monthly Goal Progress Notification";
                String body = buildEmailBody(goal.getGoalName(), monthsLeft);
                emailService.sendEmail(email, subject, body);
            }
        });
    }

    private String buildEmailBody(String goalName, long monthsLeft) {
        if (monthsLeft > 0) {
            return String.format(
                    "Dear Customer,\n\n" +
                            "This is a reminder for your goal: '%s'. You have %d months remaining to achieve your target.\n\n" +
                            "Keep up the good work!\n\nBest regards,\nFinTrack Team",
                    goalName, monthsLeft
            );
        } else {
            return String.format(
                    "Dear Customer,\n\n" +
                            "Congratulations! You have achieved your goal: '%s'.\n\n" +
                            "Thank you for using FinTrack.\n\nBest regards,\nFinTrack Team",
                    goalName
            );
        }
    }

}
