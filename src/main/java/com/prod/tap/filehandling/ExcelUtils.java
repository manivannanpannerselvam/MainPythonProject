package com.prod.tap.filehandling;
import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ExcelUtils {

    @Autowired
    private Configvariable configvariable;
    private FileInputStream file = null;
    private XSSFWorkbook workbook = null;
    private XSSFSheet sheet = null;


    public ExcelUtils() {
    }

    public XSSFWorkbook getWorkBook(String filePath, int sheetNum) {
        try {
            this.file = new FileInputStream(new File(filePath));
            this.workbook = new XSSFWorkbook(this.file);
            this.sheet = this.workbook.getSheetAt(sheetNum);
        } catch (FileNotFoundException var4) {
            throw new TapException(TapExceptionType.IO_ERROR, "File not found [{}]", new Object[]{filePath});
        } catch (IOException var5) {
            throw new TapException(TapExceptionType.IO_ERROR, "NOt able to create workbook for [{}]", new Object[]{filePath});
        }

        return this.workbook;
    }

    public Map<String, Integer> getExcelColumns(String filePath, int sheetNum) {
        Map<String, Integer> columnsIndex = new HashMap();
        this.getWorkBook(filePath, sheetNum);
        int cols = this.sheet.getRow(0).getLastCellNum();

        for(int iCellCount = 0; iCellCount < cols; ++iCellCount) {
            Cell cellHeader = this.sheet.getRow(0).getCell(iCellCount);
            columnsIndex.put(cellHeader.getStringCellValue(), iCellCount);
        }

        return columnsIndex;
    }

    public void closeWorkBookAndFile() {
        try {
            this.file.close();
            this.workbook.close();
        } catch (IOException var2) {
            throw new TapException(TapExceptionType.IO_ERROR, "NOt able to close workbook", new Object[0]);
        }
    }

    public Map<String, Map<String, String>> readExcelDataIntoMap(String filePath, int sheetNum, String columnName) {
        HashMap dataMap = new HashMap();

        try {
            Map<String, Integer> columns = this.getExcelColumns(filePath, sheetNum);
            int rows = this.sheet.getLastRowNum();
            int cols = columns.size();

            for(int iRow = 1; iRow <= rows; ++iRow) {
                Map<String, String> rowData = new HashMap();
                Cell cell = this.sheet.getRow(iRow).getCell((Integer)columns.get(columnName));

                for(int iCellCount = 0; iCellCount < cols; ++iCellCount) {
                    String cellVal = "";
                    Cell cellData = this.sheet.getRow(iRow).getCell(iCellCount);
                    Cell cellHeader = this.sheet.getRow(0).getCell(iCellCount);
                    if (!cellHeader.getStringCellValue().equalsIgnoreCase(columnName)) {
                        if (cellData != null) {
                            if (cellData.getCellType() == CellType.STRING) {
                                cellVal = cellData.getStringCellValue().toString();
                            } else if (cellData.getCellType() == CellType.NUMERIC) {
                                cellVal = Double.toString((double)((int)cellData.getNumericCellValue()));
                            }
                        }

                        rowData.put(this.configvariable.expandValue(cellHeader.getStringCellValue()), this.configvariable.expandValue(cellVal));
                    }
                }

                dataMap.put(this.configvariable.expandValue(cell.getStringCellValue()), rowData);
            }

            this.closeWorkBookAndFile();
            return dataMap;
        } catch (Exception var15) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to read Excel data into Map", new Object[0]);
        }
    }

    public void createNewExcel(String sheetName, Map<String, Map<String, String>> dataMap, String filePath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        Set<String> keyset = dataMap.keySet();
        int rownum = 0;
        Iterator var8 = keyset.iterator();

        while(var8.hasNext()) {
            String key = (String)var8.next();
            Row row = sheet.createRow(rownum++);
            Map<String, String> objArr = (Map)dataMap.get(key);
            int cellnum = 0;
            Iterator var13 = objArr.keySet().iterator();

            while(var13.hasNext()) {
                String obj = (String)var13.next();
                Cell cell = row.createCell(cellnum++);
                cell.setCellValue((String)objArr.get(obj));
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(new File(filePath));
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (Exception var16) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to write to Excel data from Map", new Object[0]);
        }
    }

    public void writeToColumnsForGivenRow(String filePath, int sheetNum, String rowToSearchVal, Map<String, String> rowData) {
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(sheetNum);
            int rows = sheet.getLastRowNum();
            int cols = sheet.getRow(0).getLastCellNum();

            for(int iRow = 1; iRow <= rows; ++iRow) {
                Cell cell = sheet.getRow(iRow).getCell(0);
                if (rowToSearchVal.equals(cell.getStringCellValue())) {
                    Iterator var12 = rowData.keySet().iterator();

                    while(var12.hasNext()) {
                        String obj = (String)var12.next();

                        for(int iCellCount = 1; iCellCount < cols; ++iCellCount) {
                            Cell cellHeader = sheet.getRow(0).getCell(iCellCount);
                            if (cellHeader.getStringCellValue().equalsIgnoreCase(this.configvariable.expandValue(obj))) {
                                String cellVal = this.configvariable.expandValue((String)rowData.get(this.configvariable.expandValue(obj)));
                                Cell cell2Update = sheet.getRow(iRow).getCell(iCellCount);
                                if (cell2Update == null) {
                                    cell2Update = sheet.getRow(iRow).createCell(iCellCount);
                                }

                                cell2Update.setCellValue(cellVal);
                            }
                        }
                    }
                }
            }

            file.close();
            FileOutputStream outputStream = new FileOutputStream(new File(filePath));
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception var18) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to update row [{}]", new Object[]{rowToSearchVal});
        }
    }

    public void writeToRowsForGivenColumn(String filePath, int sheetNum, String columnName, String columnNameToSearch, Map<String, String> rowData) {
        try {
            Map<String, Integer> columns = this.getExcelColumns(filePath, sheetNum);
            int rows = this.sheet.getLastRowNum();
            int cols = columns.size();
            String[] listOfColumns = columnName.split("##");

            label50:
            for(int iRow = 1; iRow <= rows; ++iRow) {
                Cell cell = this.sheet.getRow(iRow).getCell((Integer)columns.get(columnNameToSearch));
                Iterator var12 = rowData.keySet().iterator();

                while(true) {
                    String obj;
                    do {
                        if (!var12.hasNext()) {
                            continue label50;
                        }

                        obj = (String)var12.next();
                    } while(!this.configvariable.expandValue(obj).equals(cell.getStringCellValue()));

                    for(int i = 0; i < listOfColumns.length; ++i) {
                        for(int iCellCount = 1; iCellCount < cols; ++iCellCount) {
                            Cell cellHeader = this.sheet.getRow(0).getCell(iCellCount);
                            if (cellHeader.getStringCellValue().equalsIgnoreCase(this.configvariable.expandValue(listOfColumns[i]))) {
                                String cellVal = this.configvariable.expandValue(((String)rowData.get(obj)).split("##")[i]);
                                Cell cell2Update = this.sheet.getRow(iRow).getCell(iCellCount);
                                cell2Update = this.sheet.getRow(iRow).createCell(iCellCount);
                                cell2Update.setCellValue(cellVal);
                                break;
                            }
                        }
                    }
                }
            }

            this.file.close();
            FileOutputStream outputStream = new FileOutputStream(new File(filePath));
            this.workbook.write(outputStream);
            this.workbook.close();
            outputStream.close();
        } catch (Exception var19) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to update rows [{}] for column [{}]", new Object[]{rowData, columnName});
        }
    }

    public void writeToAllRowsForGivenColumn(String filePath, int sheetNum, String columnName) {
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(sheetNum);
            int rows = sheet.getLastRowNum();
            int cols = sheet.getRow(0).getLastCellNum();
            String[] columns = columnName.split("##");

            for(int iRow = 1; iRow <= rows; ++iRow) {
                for(int iCount = 0; iCount < columns.length; ++iCount) {
                    for(int iCellCount = 0; iCellCount < cols; ++iCellCount) {
                        Cell cellHeader = sheet.getRow(0).getCell(iCellCount);
                        if (cellHeader.getStringCellValue().equalsIgnoreCase(this.configvariable.expandValue(columns[iCount]))) {
                            Cell cell2Update = sheet.getRow(iRow).getCell(iCellCount);
                            if (cell2Update == null) {
                                cell2Update = sheet.getRow(iRow).createCell(iCellCount);
                            }

                            String cellVal = this.configvariable.expandValue(cell2Update.getStringCellValue());
                            cell2Update.setCellValue(cellVal);
                            break;
                        }
                    }
                }
            }

            file.close();
            FileOutputStream outputStream = new FileOutputStream(new File(filePath));
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception var16) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to update rows for column [{}]", new Object[]{columnName});
        }
    }
}
