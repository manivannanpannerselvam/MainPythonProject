package com.prod.tap.selenium;

import com.prod.tap.config.Configvariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContext {
    private final String DEFAULT_WEB_BROWSER = "chrome";
    private final String WEB_BROWSER_TYPE = "web.browser.type";
    private final String DEFAULT_IMPLICIT_WAIT = "20";
    private final String WEB_BROWSER_CUSTOMIZED_IMPLICIT_WAIT = "web.implicit.wait";
    private final String DEFAULT_BROWSER_MODE = "false";
    private final String WEB_BROWSER_MODE = "web.driver.headless";
    private final String WEB_DEFAULT_DOWNLOAD_LOCATION = "web.browser.download";

    @Autowired
    Configvariable configvariable;

    private void setWebBrowserType() {
        if (System.getProperty(WEB_BROWSER_TYPE) == null) {
            configvariable.setStringVariable(DEFAULT_WEB_BROWSER, WEB_BROWSER_TYPE);
        } else {
            configvariable.setStringVariable(System.getProperty(WEB_BROWSER_TYPE).toLowerCase(), WEB_BROWSER_TYPE);
        }
    }

    private void setDriverImplicitTimeout() {
        if (configvariable.getStringVar(WEB_BROWSER_CUSTOMIZED_IMPLICIT_WAIT).isEmpty()) {
            configvariable.setStringVariable(DEFAULT_IMPLICIT_WAIT, WEB_BROWSER_CUSTOMIZED_IMPLICIT_WAIT);
        }
    }

    private void setBrowserMode() {
        if (System.getProperty(WEB_BROWSER_MODE) == null) {
            configvariable.setStringVariable(DEFAULT_BROWSER_MODE, WEB_BROWSER_MODE);
        } else {
            configvariable.setStringVariable(System.getProperty(WEB_BROWSER_MODE).toLowerCase(), WEB_BROWSER_MODE);
        }
    }

    private void setDefaultDownloadLocationForBrowser() {
        if (configvariable.getStringVar(WEB_DEFAULT_DOWNLOAD_LOCATION).isEmpty()) {
            configvariable.setStringVariable(System.getProperty("user.dir"), WEB_DEFAULT_DOWNLOAD_LOCATION);
        }
    }

    public void initializeVariables() {
        setWebBrowserType();
        setDriverImplicitTimeout();
        setBrowserMode();
        setDefaultDownloadLocationForBrowser();
    }

    public String getBrowserType() {
        return configvariable.getStringVar(WEB_BROWSER_TYPE);
    }

}
