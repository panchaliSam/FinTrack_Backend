package com.fintrack.controller;

import com.fintrack.entity.Transaction;
import com.fintrack.service.ExcelExportService;
import com.fintrack.service.ReportService;
import com.fintrack.type.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/total-income")
    public double calculateTotalIncome() {
        return reportService.calTotalIncome();
    }

    @GetMapping("/total-expense")
    public double calculateTotalOutcome() {
        return reportService.calTotalExpense();
    }

    @GetMapping("/total-income/year")
    public double calculateTotalIncomeYear(@RequestParam("year") int year) {return reportService.calTotalIncomeGivingYear(year);}

    @GetMapping("/total-expense/year")
    public double calculateTotalExpenseYear(@RequestParam("year") int year) {return reportService.calTotalExpenseGivingYear(year);}

    @GetMapping("total-budget-income/year")
    public double calculateTotalBudgetIncomeYear(@RequestParam("year") int year) {return reportService.getBudgetIncomeAmountByYear(year);}

    @GetMapping("total-budget-expense/year")
    public double calculateTotalBudgetExpenseYear(@RequestParam("year") int year) {return reportService.getBudgetExpenseAmountByYear(year);}


    @GetMapping("/spending-trends")
    public Map<String, Double> getSpendingTrends(
            @RequestParam String userId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ) {
        return reportService.getSpendingTrends(userId, startDate, endDate);
    }

    @GetMapping("/income-vs-expenses")
    public Map<Category, Double> getIncomeVsExpenses(
            @RequestParam String userId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ) {
        return reportService.getIncomeVsExpenses(userId, startDate, endDate);
    }

    @GetMapping("/filter")
    public List<Transaction> filterTransactions(
            @RequestParam String userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ) {
        return reportService.filterTransactions(userId, category, tag, startDate, endDate);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam String userId, @RequestParam int year) {
        try {
            System.out.println("📥 Received download request: UserID=" + userId + ", Year=" + year);

            String filePath = excelExportService.generateFinancialReport(userId, year);
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("❌ File not found at: " + filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            System.out.println("✅ File found: " + filePath);

            byte[] fileBytes = Files.readAllBytes(file.toPath());
            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
