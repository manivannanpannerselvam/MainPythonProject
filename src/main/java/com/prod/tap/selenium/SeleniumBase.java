package com.prod.tap.selenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeleniumBase {
    private static final Logger logger = Logger.getLogger(SeleniumBase.class);
    public static WebDriver driver;

    @Autowired
    private ExecuteCommand executeCommand;

    @Autowired
    private ExecutionContext executionContext;


    public void initializeSeleniumFramework() {
        executionContext.initializeVariables();
    }

    public void launchApplication(String url, String username, String password) {
        String URL = url;
        if (!username.isEmpty() && !password.isEmpty()) {
            String[] parts = url.split("http://");
            URL = "http://" + username + ":" + password + "@" + parts[1];
        }
        driver = executeCommand.getDriverForRequiredBrowser(executionContext.getBrowserType());
        executeCommand.executeCommand(driver, Commands.OPEN_URL, null, URL, "");
    }

    public void quitDriver() {
        executeCommand.executeCommand(driver, Commands.CLOSE_ALL_BROWSER, null, "", "");
    }

}
