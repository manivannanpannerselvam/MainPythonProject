package com.prod.tap.selenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebTableMethods {
    private static final Logger logger = Logger.getLogger(WebTableMethods.class);

    public int getRowsCountInTable(WebDriver driver, String locator) {
        List rows = driver.findElements(By.xpath(locator + "/table/tbody/tr/td[1]"));
        logger.info("No of rows are : " + rows.size());
        return rows.size();
    }

    public int getColumnsCountInTable(WebDriver driver, String locator) {
        List col = driver.findElements(By.xpath(locator + "/table/thead/tr/th"));
        logger.info("No of cols are : " + col.size());
        return col.size();
    }

    public Map<String, Integer> getTableColumnIndex(WebDriver driver, String locator) {
        Map<String, Integer> columnsIndex = new HashMap<String, Integer>();
        List<WebElement> col = driver.findElements(By.xpath(locator + "/table/thead/tr/th"));
        for (int iCount = 0; iCount < col.size(); iCount++) {
            columnsIndex.put(col.get(iCount).getText(), iCount + 1);
        }
        return columnsIndex;
    }

    public int findTableRow(WebDriver driver, String locator, Map<String, String> columnMap) {
        int rowIndex = 0;
        boolean present = false;
        Map<String, Integer> columns = getTableColumnIndex(driver, locator);
        int rows = getRowsCountInTable(driver, locator);
        for (int rowCount = 1; rowCount <= rows; rowCount++) {
            for (String key : columnMap.keySet()) {
                if (getTDCellValue(driver, locator, rowCount, columns.get(key)).contains(columnMap.get(key))) {
                    present = true;
                } else {
                    present = false;
                    break;
                }
            }

            if (present) {
                rowIndex = rowCount;
                break;
            } else {
                rowIndex = 0;
            }
        }
        return rowIndex;
    }

    public String getTDCellValue(WebDriver driver, String locator, int row, int col) {
        WebElement cell = driver.findElement(By.xpath(locator + "/table/tbody/tr[" + row + "]/td[" + col + "]"));
        return cell.getText();
    }

    public List<String> getTableColumnData(WebDriver driver, String locator, String columnName) {
        List<String> arrList = new ArrayList<String>();
        Map<String, Integer> columns = getTableColumnIndex(driver, locator);
        int rows = getRowsCountInTable(driver, locator);
        int column = columns.get(columnName);
        for (int rowCount = 1; rowCount <= rows; rowCount++) {
            arrList.add(getTDCellValue(driver, locator, rowCount, column));
        }
        return arrList;
    }

    public Map<String, Integer> getTableColumnIndexTypeTD(WebDriver driver, String locator) {
        Map<String, Integer> columnsIndex = new HashMap();
        List<WebElement> col = driver.findElements(By.xpath(locator + "/table/thead/tr/td"));

        for(int iCount = 0; iCount < col.size(); ++iCount) {
            columnsIndex.put(((WebElement)col.get(iCount)).getText(), iCount + 1);
        }

        return columnsIndex;
    }

    public List<String> getTableColumnDataForTDHeaders(WebDriver driver, String locator, String columnName) {
        List<String> arrList = new ArrayList<String>();
        Map<String, Integer> columns = getTableColumnIndexTypeTD(driver, locator);
        int rows = getRowsCountInTable(driver, locator);
        int column = columns.get(columnName);
        for (int rowCount = 1; rowCount <= rows; rowCount++) {
            arrList.add(getTDCellValue(driver, locator, rowCount, column));
        }
        return arrList;
    }

    public List<String> getTableHeaderNameForTypeTD(WebDriver driver,String locator) {
        List<String> tableHeadersList = new ArrayList();
        List<WebElement> col = driver.findElements(By.xpath(locator + "/table/thead/tr/td"));
        for (int iCount = 0; iCount < col.size(); iCount++) {
            String header = col.get(iCount).getText();
            if (!header.isEmpty()) {
                tableHeadersList.add(col.get(iCount).getText());
            }
        }
        return tableHeadersList;
    }


}
