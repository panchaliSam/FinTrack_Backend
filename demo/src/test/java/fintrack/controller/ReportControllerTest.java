package fintrack.controller;

import com.fintrack.controller.ReportController;
import com.fintrack.entity.Transaction;
import com.fintrack.service.ExcelExportService;
import com.fintrack.service.ReportService;
import com.fintrack.type.Category;
import com.fintrack.type.RecurrenceFrequency;
import com.fintrack.type.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private ExcelExportService excelExportService;

    @InjectMocks
    private ReportController reportController;

    private final String userId = "user123";
    private final int year = 2024;

    @BeforeEach
    void setUp() {
    }

    @Test
    void calculateTotalIncome() {
        when(reportService.calTotalIncome()).thenReturn(5000.00);

        double totalIncome = reportController.calculateTotalIncome();

        assertEquals(5000.00, totalIncome);
        verify(reportService, times(1)).calTotalIncome();
    }

    @Test
    void calculateTotalOutcome() {
        when(reportService.calTotalExpense()).thenReturn(2500.00);

        double totalOutcome = reportController.calculateTotalOutcome();

        assertEquals(2500.00, totalOutcome);
        verify(reportService, times(1)).calTotalExpense();
    }

    @Test
    void calculateTotalIncomeYear() {
        when(reportService.calTotalIncomeGivingYear(year)).thenReturn(60000.00);

        double totalIncomeYear = reportController.calculateTotalIncomeYear(year);

        assertEquals(60000.00, totalIncomeYear);
        verify(reportService, times(1)).calTotalIncomeGivingYear(year);
    }

    @Test
    void calculateTotalExpenseYear() {
        when(reportService.calTotalExpenseGivingYear(year)).thenReturn(30000.00);

        double totalExpenseYear = reportController.calculateTotalExpenseYear(year);

        assertEquals(30000.00, totalExpenseYear);
        verify(reportService, times(1)).calTotalExpenseGivingYear(year);
    }

    @Test
    void calculateTotalBudgetIncomeYear() {
        when(reportService.getBudgetIncomeAmountByYear(year)).thenReturn(70000.00);

        double budgetIncome = reportController.calculateTotalBudgetIncomeYear(year);

        assertEquals(70000.00, budgetIncome);
        verify(reportService, times(1)).getBudgetIncomeAmountByYear(year);
    }

    @Test
    void calculateTotalBudgetExpenseYear() {
        when(reportService.getBudgetExpenseAmountByYear(year)).thenReturn(35000.00);

        double budgetExpense = reportController.calculateTotalBudgetExpenseYear(year);

        assertEquals(35000.00, budgetExpense);
        verify(reportService, times(1)).getBudgetExpenseAmountByYear(year);
    }

    @Test
    void getSpendingTrends() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 0, 0);

        Map<String, Double> mockTrends = Map.of(
                "Food", 1200.00,
                "Rent", 8000.00
        );

        when(reportService.getSpendingTrends(userId, startDate, endDate)).thenReturn(mockTrends);

        Map<String, Double> response = reportController.getSpendingTrends(userId, startDate, endDate);

        assertEquals(mockTrends, response);
        verify(reportService, times(1)).getSpendingTrends(userId, startDate, endDate);
    }

    @Test
    void getIncomeVsExpenses() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 0, 0);

        Map<Category, Double> mockComparison = Map.of(
                Category.INCOME, 60000.00,
                Category.EXPENSE, 30000.00
        );

        when(reportService.getIncomeVsExpenses(userId, startDate, endDate)).thenReturn(mockComparison);

        Map<Category, Double> response = reportController.getIncomeVsExpenses(userId, startDate, endDate);

        assertEquals(mockComparison, response);
        verify(reportService, times(1)).getIncomeVsExpenses(userId, startDate, endDate);
    }

    @Test
    void downloadReport_FileNotFound() throws IOException {
        when(excelExportService.generateFinancialReport(userId, year)).thenReturn("/invalid/path.xlsx");

        ResponseEntity<Resource> response = reportController.downloadReport(userId, year);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(excelExportService, times(1)).generateFinancialReport(userId, year);
    }

    @Test
    void downloadReport_ServerError() throws IOException {
        when(excelExportService.generateFinancialReport(userId, year)).thenThrow(new IOException("Server Error"));

        ResponseEntity<Resource> response = reportController.downloadReport(userId, year);

        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(excelExportService, times(1)).generateFinancialReport(userId, year);
    }
}