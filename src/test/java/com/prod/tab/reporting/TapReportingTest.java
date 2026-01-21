package com.prod.tab.reporting;

import com.prod.tap.reporting.TapReporting;
import com.prod.tap.filehandling.FileReaderUtil;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class TapReportingTest {

    TapReporting tapReporting = new TapReporting();

    @BeforeTest
    public void setUp() {
        FileReaderUtil.createDirectory(System.getProperty("user.dir") + "/reports/cucumber/");
        FileReaderUtil.copyFilesToDirectory("target/test-classes/testFile", System.getProperty("user.dir") + "/reports/cucumber/");
    }

    @Test
    public void testgenerateReportForJsonFilesInLocal() throws IOException {
        String filePath = null;
        TapReporting.generateReportForJsonFiles(filePath);
    }

    @Test
    public void testgenerateReportForJsonFilesDeviceFarm() throws IOException {
        String filePath = "target/test-classes/reports";
        TapReporting.generateReportForJsonFiles(filePath);
    }

    @Test
    public void testgenerateReportForJsonFilesDeviceId() throws IOException {
        String filePath = null;
        System.setProperty("device.udid", "cb12315627");
        FileReaderUtil.createDirectory(System.getProperty("user.dir") + "/reports/cucumber/cb12315627");
        FileReaderUtil.copyFilesToDirectory("target/test-classes/testFile", System.getProperty("user.dir") + "/reports/cucumber/cb12315627/");
        TapReporting.generateReportForJsonFiles(filePath);
    }

    @Test
    public void testLocation() {
        tapReporting.getScreenShotLocation(System.getProperty("user.dir") + "./reports/cucumber/cucumber.json");
    }


    @Test
    public void testDetailedReport() {
        FileReaderUtil.deleteFile("reports/detail-test-test-results.pdf");
        TapReporting.detailedReport("reports/cucumber/cucumber.json", "detail-report");
    }

}
