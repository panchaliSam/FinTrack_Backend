package com.fintrack;

import com.fintrack.service.*;
import com.fintrack.type.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.time.LocalDateTime;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class FinTrackApplication implements CommandLineRunner {

	private final UserService userService;
	private final TransactionService transactionService;
	private final BudgetService budgetService;
	private final GoalService goalService;
	private final SavingsService savingsService;

	public FinTrackApplication(UserService userService, TransactionService transactionService,
							   BudgetService budgetService, GoalService goalService,
							   SavingsService savingsService) {
		this.userService = userService;
		this.transactionService = transactionService;
		this.budgetService = budgetService;
		this.goalService = goalService;
		this.savingsService = savingsService;
	}

	public static void main(String[] args) {
		SpringApplication.run(FinTrackApplication.class, args);
	}

	@Override
	public void run(String... args) {
//		insertSampleUser();
		insertSampleTransaction();
//		insertSampleBudget();
//		insertSampleSavings();
//		insertSampleGoal();
	}

	/**
	 * Inserts a sample user into MongoDB.
	 */
	private void insertSampleUser() {
		userService.createUser("Kamal", "Perera", "kamalPerera@gmail.com", "*kamal123#", Role.ADMIN);
		System.out.println("Sample user inserted successfully.");
	}

	/**
	 * Inserts a sample transaction into MongoDB.
	 */
	private void insertSampleTransaction() {
		transactionService.createTransaction(
				"67cfc89ebc863110b8f4a325",
				Tag.BUSINESS,
				Category.INCOME,
				"This is a sample transaction",
				2000000.00,
				LocalDateTime.now(),
				true,
				RecurrenceFrequency.DAILY
		);

		System.out.println("Sample transaction inserted successfully.");
	}

	/**
	 * Inserts a sample budget into MongoDB.
	 */
	private void insertSampleBudget() {
		budgetService.createBudget(
				"67cfc89ebc863110b8f4a325",
				Category.EXPENSE,
				1500.00,
				LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0),
				LocalDateTime.of(2026, 1, 1, 0, 0, 0, 0),
				BudgetStatus.ACTIVE
		);

		System.out.println("Sample budget inserted successfully.");
	}

	/**
	 * Inserts a sample savings goal into MongoDB.
	 */
	private void insertSampleSavings() {
		savingsService.createSavings(10000);
		System.out.println("Sample savings inserted successfully.");
	}

	/**
	 * Inserts a sample financial goal into MongoDB.
	 */
	private void insertSampleGoal() {
		goalService.createGoal(
				"67cfc89ebc863110b8f4a325",
				"Buying a car",
				2000,
				0,
				LocalDateTime.of(2025, 12, 1, 0, 0),
				0.1
		);

		System.out.println("Sample financial goal inserted successfully.");
	}
}