package com.fintrack.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ExcelExportService {

    @Autowired
    private ReportService reportService;

    private static final String FILE_DIRECTORY = "generated-reports/";

    public String generateFinancialReport(String userId, int year) throws IOException {
        System.out.println("🔄 Starting report generation for UserID: " + userId + ", Year: " + year);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Financial Report");

        try {
            createHeaderRow(sheet, workbook);

            double totalIncome = reportService.calTotalIncomeGivingYear(year);
            double totalExpense = reportService.calTotalExpenseGivingYear(year);
            double budgetedIncome = reportService.getBudgetIncomeAmountByYear(year);
            double budgetedExpense = reportService.getBudgetExpenseAmountByYear(year);

            System.out.println("✅ Financial Data Fetched Successfully:");
            System.out.println("Total Income: " + totalIncome);
            System.out.println("Total Expense: " + totalExpense);
            System.out.println("Budgeted Income: " + budgetedIncome);
            System.out.println("Budgeted Expense: " + budgetedExpense);

            Object[][] data = {
                    {"Total Income", totalIncome},
                    {"Total Expense", totalExpense},
                    {"Budgeted Income", budgetedIncome},
                    {"Budgeted Expense", budgetedExpense},
            };

            populateDataRows(sheet, data);

            addSpendingTrends(sheet, userId, year);

            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            File directory = new File(FILE_DIRECTORY);
            if (!directory.exists() && !directory.mkdirs()) {
                System.out.println("❌ Failed to create directory: " + FILE_DIRECTORY);
                throw new IOException("Could not create report directory");
            }

            String filePath = FILE_DIRECTORY + "Financial_Report_" + year + ".xlsx";
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("✅ Report successfully saved at: " + filePath);
            }

            return filePath;

        } catch (Exception e) {
            System.out.println("❌ Error generating report: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error generating report", e);
        } finally {
            workbook.close();
        }
    }

    private void createHeaderRow(Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Category", "Total Amount"};
        CellStyle headerStyle = getHeaderStyle(workbook);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void populateDataRows(Sheet sheet, Object[][] data) {
        int rowNum = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) rowData[0]);
            row.createCell(1).setCellValue((Double) rowData[1]);
        }
    }

    private void addSpendingTrends(Sheet sheet, String userId, int year) {
        int rowNum = sheet.getLastRowNum() + 2;

        Row trendHeader = sheet.createRow(rowNum++);
        trendHeader.createCell(0).setCellValue("Date");
        trendHeader.createCell(1).setCellValue("Spending");

        Map<String, Double> spendingTrends = reportService.getSpendingTrends(userId,
                LocalDateTime.of(year, 1, 1, 0, 0),
                LocalDateTime.of(year, 12, 31, 23, 59));

        if (spendingTrends.isEmpty()) {
            System.out.println("ℹ️ No spending trends available for UserID: " + userId);
        }

        for (Map.Entry<String, Double> entry : spendingTrends.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
    }

    private CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}