package utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelUtils {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/" + ConfigReader.get("excelPath");
    
    // Reading File
    public static String getCellData(String sheetName, int rowIndex, int colIndex) {

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
            Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException(
                    "Sheet '" + sheetName + "' not found in TestData.xlsx"
                );
            }
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                throw new RuntimeException(
                    "Row " + rowIndex + " not found in sheet: " + sheetName
                );
            }
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                return "";
            }
            
            // converts cell data to string irrespective of the data type 
            DataFormatter formatter = new DataFormatter();
            return formatter.formatCellValue(cell).trim();
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to read Excel file: " + FILE_PATH, e
            );
        }
    }
    // write data in excel
    public static void setCellData(String sheetName, int rowIndex,
                                   int colIndex, String value) {

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            // check if the sheet is null
            if (sheet == null) {
                throw new RuntimeException(
                    "Sheet '" + sheetName + "' not found in TestData.xlsx"
                );
            }
            // check if the row is null and then create
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            // Same for cell — create if it doesn't exist
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                cell = row.createCell(colIndex);
            }
            cell.setCellValue(value);

            try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to write to Excel file: " + FILE_PATH, e
            );
        }
    }
}