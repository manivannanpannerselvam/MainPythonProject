package com.prod.tap.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.cucumber.listener.Reporter;
import com.github.mkolisnyk.cucumber.reporting.CucumberDetailedResults;
import com.github.mkolisnyk.cucumber.reporting.CucumberFeatureOverview;
import com.github.mkolisnyk.cucumber.reporting.CucumberUsageReporting;
import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import com.prod.tap.filehandling.FileReaderUtil;
import cucumber.api.Scenario;
import cucumber.runtime.ScenarioImpl;
import gherkin.formatter.model.Result;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static gherkin.formatter.model.Result.FAILED;
import static gherkin.formatter.model.Result.PASSED;
import static junit.framework.TestCase.assertTrue;

public class TapReporting {

    private static final Logger LOGGER = Logger.getLogger(TapReporting.class);

    private ExtentHtmlReporter extentHtmlReporter;
    private ExtentReports extentReports;


    public static void generateReportForJsonFiles(String devicefarmLogDir) {

        String logFileDirectory = null;
        String reportDirectory = null;
        String projectName = "Test Automation ";

        if (devicefarmLogDir != null) {
            logFileDirectory = devicefarmLogDir;
            LOGGER.info("log directory is :" + logFileDirectory);
            FileReaderUtil.copyDirectory("reports/cucumber", devicefarmLogDir);
            reportDirectory = "reports/cucumber";
        } else {
            if (System.getProperty("device.udid") != null) {
                FileReaderUtil.createDirectory(System.getProperty("user.dir") + "/src/test/resources/reports/summary/" + System.getProperty("device.udid"));

                logFileDirectory = System.getProperty("user.dir") + "/src/test/resources/reports/summary/" + System.getProperty("device.udid");
                reportDirectory = "reports/cucumber/" + System.getProperty("device.udid");
                projectName = projectName + "-" + System.getProperty("device.udid");
            } else {
                logFileDirectory = System.getProperty("user.dir") + "/src/test/resources/reports/summary";
                reportDirectory = "reports/cucumber";
            }
        }

        File reportOutputDirectory = new File(logFileDirectory);
        List<String> jsonFiles = getAllJsonFilesUnderTarget(reportDirectory);
        assertTrue(jsonFiles.size() > 0);
        String buildNumber = "1";

        Configuration configuration = new Configuration(reportOutputDirectory, projectName + Configvariable.globalPropertyMap.get("project.name"));
        configuration.setRunWithJenkins(false);
        configuration.setBuildNumber(buildNumber);

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();

    }

    private static List<String> getAllJsonFilesUnderTarget(String folderLocation) {

        List<String> jsonFiles = new ArrayList<>();
        File directory = new File(folderLocation);
        File[] files = directory.listFiles((file, name) -> name.endsWith(".json"));
        if (files != null && files.length > 0) {
            for (File f : files) {
                String filePath = folderLocation + "/" + f.getName();
                jsonFiles.add(filePath);

                Path path = Paths.get(filePath);
                File file = path.toFile();

                assertTrue(file.exists());
                LOGGER.info(String.format("Found json file: %s with size of %d at path %s",
                        filePath, file.length(), file.getAbsolutePath()));
            }
        }
        return jsonFiles;
    }


    public void customExtentReport(String reportLocation) {
        extentHtmlReporter = new ExtentHtmlReporter(new File(reportLocation));
        extentHtmlReporter.config().setDocumentTitle("Automation Test Report");
        extentHtmlReporter.config().setReportName("Automation Test Report");
        extentHtmlReporter.config().setTheme(Theme.STANDARD);
        extentReports = new ExtentReports();
        extentReports.attachReporter(extentHtmlReporter);
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReports.setSystemInfo("User Name", System.getProperty("user.name"));
    }

    public void createTest(Scenario scenario, String screenShotFile) {
        ExtentTest test = null;
        if (scenario != null) {
            String testName = getScenarioTitle(scenario);
            test = extentReports.createTest(testName);


            switch (scenario.getStatus()) {
                case PASSED:
                    test.pass(MarkupHelper.createLabel("Test Case is Passed : ", ExtentColor.GREEN));
//                    extentReports.createTest(testName).pass("Passed");
                    break;
                case FAILED:
                    try {
                        test.fail(MarkupHelper.createLabel("Test Case is Failed : ", ExtentColor.RED));
                        test.error(logError(scenario));
                        test.addScreenCaptureFromPath(screenShotFile);
                    } catch (Exception e) {
                        throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to add screenshot to report");
                    }
                    break;
                default:
                    test.skip(MarkupHelper.createLabel("Test Case is Skipped : ", ExtentColor.YELLOW));
//                    extentReports.createTest(testName).skip("Skipped");
            }
        }
    }

    private Throwable logError(Scenario scenario) {
        Field field = FieldUtils.getField(((ScenarioImpl) scenario).getClass(), "stepResults", true);
        field.setAccessible(true);
        try {
            ArrayList<Result> results = (ArrayList<Result>) field.get(scenario);
            for (Result result : results) {
                if (result.getError() != null)
                    return result.getError();
            }
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Error while logging the error");
        }
        return null;
    }

    private String getScenarioTitle(Scenario scenario) {
        return scenario.getName();
    }

    public String getScreenShotLocation(String location) {
        return "file:///" + location.replaceAll("\\\\", "//");
    }

    public void writeToReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    public String getReportConfigPath() {
        String reportConfigPath = System.getProperty("user.dir") + "src/main/java/config/extent-config.xml";
        if (reportConfigPath != null) return reportConfigPath;
        else
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Report Config Path not specified in the .properties file for the Key:reportConfigPath");
    }

    public void writeExtentReport() {
        Reporter.loadXMLConfig(new File(getReportConfigPath()));
        Reporter.setSystemInfo("User Name", System.getProperty("user.name"));
        Reporter.setSystemInfo("Time Zone", System.getProperty("user.timezone"));
        Reporter.setSystemInfo("Machine", System.getProperty("os"));
        Reporter.setSystemInfo("Selenium", "3.7.0");
        Reporter.setSystemInfo("Maven", "3.5.2");
        Reporter.setSystemInfo("Java Version", "1.8.0_151");
    }

    public static void overviewReport(String jsonReportFilePath, String overViewreportName) {
        CucumberFeatureOverview results = new CucumberFeatureOverview();
        results.setOutputDirectory("reports");
        results.setOutputName(overViewreportName);
        results.setSourceFile(jsonReportFilePath);
        try {
            results.execute(true, new String[]{"pdf", "png"});
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to generate overview report [{}]", e.getMessage());
        }
    }

    public static void detailedReport(String jsonReportFilePath, String detailReportName) {
        CucumberDetailedResults results = new CucumberDetailedResults();
        results.setOutputDirectory("reports/");
        results.setOutputName(detailReportName);
        results.setSourceFile(jsonReportFilePath);
        try {
            results.execute(true, new String[]{"pdf", "png"});
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to generate detailed report [{}]", e.getMessage());
        }
    }

    public static void usageReport(String usageJsonReportPath, String usageReportName) {
        CucumberUsageReporting results = new CucumberUsageReporting();
        results.setOutputDirectory("reports");
        results.setOutputName(usageReportName);
        results.setSourceFile(usageJsonReportPath);
        results.setJsonUsageFiles(new String[]{"reports/cucumber-usage.json"});
        try {
            results.execute(new String[]{"pdf", "png"});
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to generate usage report [{}]", e.getMessage());
        }
    }
}