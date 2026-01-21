package com.prod.tap.driver;

import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.log4j.Logger;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * contains all the methods to create a new session and destroy the
 * session after the test(s) execution is over. Each test extends
 * this class.
 */
@Component
public class TapDriver {
    private static final Logger LOGGER = Logger.getLogger(TapDriver.class);

    @Autowired
    Configvariable configvariable;

    public WebDriver driver = null;
    protected static Properties lobConfigProp = new Properties();
    public static Properties localeConfigProp = new Properties();
    protected FileInputStream configFis;
    protected FileInputStream lobConfigFis;
    protected FileInputStream localeConfigFis;
    private Properties configProp = new Properties();
    private String OS;
    public DesiredCapabilities capabilities;

    private static String APPIUM_SERVER_URL = "http://127.0.0.1:4723/wd/hub";
    private static int DEFAULT_IMPLICIT_WAIT = 2;
    public static boolean NO_RESET = false;
    private static String APP_LANGUAGE = System.getProperty("pulse.language");
    private static String APP_COUNTRY = System.getProperty("pulse.country");
    private static String PROXY_USER = System.getProperty("proxy.user");
    private static String PROXY_PASS = System.getProperty("proxy.pass");
    private static String PROXY_URL = "http://10.163.39.77:8080";


    /**
     * this method starts Appium server. Calls startAppiumServer method to start the session depending upon your OS.
     *
     * @throws Exception Unable to start appium server
     */
    //@BeforeSuite
    public void invokeAppium() throws Exception {
        String OS = System.getProperty("os.name").toLowerCase();
        try {
            startAppiumServer(OS);
            LOGGER.info("Appium server started successfully");
        } catch (Exception e) {
            LOGGER.warn("Unable to start appium server");
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Unable to start appium server");
        }
    }

    /**
     * this method stops Appium server.Calls stopAppiumServer method to
     * stop session depending upon your OS.
     *
     * @throws Exception Unable to stop appium server
     */
    //@AfterSuite
    public void stopAppium() throws Exception {
        String OS = System.getProperty("os.name").toLowerCase();
        try {
            stopAppiumServer(OS);
            LOGGER.info("Appium server stopped successfully");
        } catch (Exception e) {
            LOGGER.warn("Unable to stop appium server");
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Unable to stop appium server");
        }
    }


    /**
     * this method creates the driver depending upon the passed parameter (android or iOS)
     * and loads the properties files (config and test data properties files).
     *
     * @param os android or iOS
     * @throws Exception issue while loading properties files or creation of driver.
     */
    //@Parameters({"os"})
    //@BeforeMethod
//    public void createDriver(String os, String appPath) throws Exception {
//
//        //propertiesFileLoad(os);
//
//        //File propertiesFile = new File(file.getAbsoluteFile() + "//src//test//resources//LOGGER4j.properties");
//        //PropertyConfigurator.configure(propertiesFile.toString());
//        LOGGER.info("--------------------------------------");
//        LOGGER.info("Creating driver for " + os);
//
//        if (os.equalsIgnoreCase("android")) {
//            androidDriver(appPath);
//            LOGGER.info("Android driver created");
//
//        } else if (os.equalsIgnoreCase("iOS")) {
//            iOSDriver(appPath);
//            LOGGER.info("iOS driver created");
//        }
//    }

    /**
     * this method quit the driver after the execution of test(s)
     */
    //@AfterMethod
    public void teardown() {
        LOGGER.info("Shutting down driver");
        driver.quit();
    }


    /**
     * this method creates the android driver
     *
     * @param buildPath - path to pick the location of the app
     * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.
     */
    public void androidDriver(String buildPath, String appPkg, String appAct) throws MalformedURLException {
//        File app = new File(buildPath);
//        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "Android-Test");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", appPkg);
        capabilities.setCapability("appActivity", appAct);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 10000);
        capabilities.setCapability("noReset", NO_RESET);
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("language", APP_LANGUAGE);
        capabilities.setCapability("locale", APP_COUNTRY);
        if (System.getProperty("device.udid") != null) {
            capabilities.setCapability("udid", System.getProperty("device.udid"));
        }
        driver = new AndroidDriver(new URL(APPIUM_SERVER_URL), capabilities);
        ((AndroidDriver) driver).resetApp();
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT, TimeUnit.SECONDS);

    }

    /**
     * this method creates the iOS driver
     *
     * @param buildPath- path to pick the location of the app
     * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.
     */
    public void iOSDriver(String buildPath, String iosBundleId, String iosUDID, String iosPlatform) throws MalformedURLException {
        //		File app = new File(buildPath);
        // 		capabilities.setCapability("app", app.getAbsolutePath());
        capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iOS-Test");
        capabilities.setCapability("platformName", "iOS");
//        capabilities.setCapability("bundleId", "com.prudential.pulse.sguat");
        capabilities.setCapability("bundleId", iosBundleId);
        capabilities.setCapability("automationName", "XCUITest");
        capabilities.setCapability("noReset", false);
        capabilities.setCapability("language", APP_LANGUAGE);
        capabilities.setCapability("locale", APP_COUNTRY);
        if (System.getenv("DEVICEFARM_LOG_DIR") == null && System.getProperty("device.udid") == null) {
            capabilities.setCapability("udid", iosUDID);
            capabilities.setCapability("platformVersion", iosPlatform);
        } else if (System.getProperty("device.udid") != null) {
            capabilities.setCapability("udid", System.getProperty("device.udid"));
            capabilities.setCapability("platformVersion", System.getProperty("device.version"));
        }
        driver = new IOSDriver(new URL(APPIUM_SERVER_URL), capabilities);
        ((IOSDriver) driver).resetApp();
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT, TimeUnit.SECONDS);
    }

    public WebDriver chromeBrowserDriver() {
        WebDriverManager.chromedriver().setup();
        DesiredCapabilities capabilities;
        WebDriver driver = null;
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "chrome-Test");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "Chrome");
        capabilities.setCapability("chromedriverExecutable", WebDriverManager.chromedriver().getBinaryPath());

//        capabilities.setCapability(CapabilityType.VERSION, "9.0");
//        capabilities.setCapability("chromedriverExecutable", System.getProperty("user.dir") + "/src/test/resources/drivers/chromedriver");
        try {
            driver = new RemoteWebDriver(new URL(APPIUM_SERVER_URL), capabilities);
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        } catch (MalformedURLException e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to create driver");
        }

        return driver;
    }

    public WebDriver createHTMLUnitDriver(String Url) {
        WebDriver driver = new HtmlUnitDriver();
        // Navigate to url
        driver.get(Url);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver createPhantomJsDriver() {
        if (configvariable.isProxyRequired()) {
            WebDriverManager.phantomjs().proxyUser(PROXY_USER).proxyPass(PROXY_PASS).proxy(PROXY_URL).setup();
        } else {
            WebDriverManager.phantomjs().setup();
        }
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        caps.setCapability("locationContextEnabled", true);
        caps.setCapability("applicationCacheEnabled", true);
        caps.setCapability("browserConnectionEnabled", true);
        caps.setCapability("localToRemoteUrlAccessEnabled", true);
        caps.setCapability("locationContextEnabled", true);
//        caps.setCapability("takesScreenshot", true);
        String[] phantomArgs = new String[]{
                "--webdriver-loglevel=NONE"
        };
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
        WebDriver driver = new PhantomJSDriver(caps);
        driver.manage().timeouts().implicitlyWait(Integer.parseInt(configvariable.getStringVar("web.implicit.wait")), TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver createChromeDriver() {
        if (configvariable.isProxyRequired()) {
            WebDriverManager.chromedriver().proxyUser(PROXY_USER).proxyPass(PROXY_PASS).proxy(PROXY_URL).setup();
        } else {
            WebDriverManager.chromedriver().setup();
        }
        Map<String, Object> prefs = new HashMap<String, Object>();
        //to switch off browser notification Pass the argument 1 to allow and 2 to block
        prefs.put("profile.default_content_setting_values.notifications", 1);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("download.default_directory", configvariable.getStringVar("web.browser.download"));
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        if ("true".equalsIgnoreCase(configvariable.getStringVar("web.driver.headless"))) {
            LOGGER.debug("Running in headless mode");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--verbose");
            options.addArguments("--no-sandbox");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--allow-insecure-localhost");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--start-maximized");
        options.setPageLoadStrategy(PageLoadStrategy.NONE);

        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Integer.parseInt(configvariable.getStringVar("web.implicit.wait")), TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }


    /**
     * this method starts the appium  server depending on your OS.
     *
     * @param os your machine OS (windows/linux/mac)
     * @throws IOException          Signals that an I/O exception of some sort has occurred
     * @throws ExecuteException     An exception indicating that the executing a subprocesses failed
     * @throws InterruptedException Thrown when a thread is waiting, sleeping,
     *                              or otherwise occupied, and the thread is interrupted, either before
     *                              or during the activity.
     */
    public void startAppiumServer(String os) throws ExecuteException, IOException, InterruptedException {
        if (os.contains("windows")) {
            CommandLine command = new CommandLine("cmd");
            command.addArgument("/c");
            command.addArgument("C:/Program Files/nodejs/node.exe");
            command.addArgument("C:/Appium/node_modules/appium/bin/appium.js");
            command.addArgument("--address", false);
            command.addArgument("127.0.0.1");
            command.addArgument("--port", false);
            command.addArgument("4723");
            command.addArgument("--full-reset", false);

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.execute(command, resultHandler);
            Thread.sleep(5000);
        } else if (os.contains("mac")) {
            CommandLine command = new CommandLine("/Applications/Appium.app/Contents/Resources/node/bin/node");
            command.addArgument("/Applications/Appium.app/Contents/Resources/node_modules/appium/bin/appium.js", false);
            command.addArgument("--address", false);
            command.addArgument("127.0.0.1");
            command.addArgument("--port", false);
            command.addArgument("4723");
            command.addArgument("--full-reset", false);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.execute(command, resultHandler);
            Thread.sleep(5000);
        } else if (os.contains("linux")) {
            //Start the appium server
            System.out.println("ANDROID_HOME : ");
            System.getenv("ANDROID_HOME");
            //	System.out.println("PATH :" +System.getenv("PATH"));
            CommandLine command = new CommandLine("/bin/bash");
            command.addArgument("-c");
            command.addArgument("~/.linuxbrew/bin/node");
            command.addArgument("~/.linuxbrew/lib/node_modules/appium/lib/appium.js", true);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.execute(command, resultHandler);
            Thread.sleep(5000); //Wait for appium server to start

        } else {
            LOGGER.info(os + "is not supported yet");
        }
    }

    /**
     * this method stops the appium  server.
     *
     * @param os your machine OS (windows/linux/mac).
     * @throws IOException      Signals that an I/O exception of some sort has occurred.
     * @throws ExecuteException An exception indicating that the executing a subprocesses failed.
     */
    public void stopAppiumServer(String os) throws ExecuteException, IOException {
        if (os.contains("windows")) {
            CommandLine command = new CommandLine("cmd");
            command.addArgument("/c");
            command.addArgument("Taskkill /F /IM node.exe");

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.execute(command, resultHandler);
        } else if (os.contains("mac os x")) {
            String[] command = {"/usr/bin/killall", "-KILL", "node"};
            Runtime.getRuntime().exec(command);
            LOGGER.info("Appium server stopped");
        }
    }


    public WebDriver getWebDriver() {
        return this.driver;
    }

    public WebDriver createIEDriver() {
        if (configvariable.isProxyRequired()) {
            WebDriverManager.iedriver().proxyUser(PROXY_USER).proxyPass(PROXY_PASS).proxy(PROXY_URL).setup();
        } else {
            WebDriverManager.iedriver().setup();
        }

        InternetExplorerOptions options = new InternetExplorerOptions();
//        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        options.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "");

        WebDriver driver = new InternetExplorerDriver(options);
        driver.manage().timeouts().implicitlyWait(Integer.parseInt(configvariable.getStringVar("web.implicit.wait")), TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver createFirefoxDriver() {
        if (configvariable.isProxyRequired()) {
            WebDriverManager.firefoxdriver().proxyUser(PROXY_USER).proxyPass(PROXY_PASS).proxy(PROXY_URL).setup();
        } else {
            WebDriverManager.firefoxdriver().setup();
        }
//        FirefoxOptions options = new FirefoxOptions();
//        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
//        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        WebDriver driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(Integer.parseInt(configvariable.getStringVar("web.implicit.wait")), TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver createSafariDriver() {
        WebDriver driver = new SafariDriver();
        driver.manage().timeouts().implicitlyWait(Integer.parseInt(configvariable.getStringVar("web.implicit.wait")), TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver createEdgeDriver() {
        if (configvariable.isProxyRequired()) {
            WebDriverManager.edgedriver().proxyUser(PROXY_USER).proxyPass(PROXY_PASS).proxy(PROXY_URL).setup();
        } else {
            WebDriverManager.edgedriver().setup();
        }

        WebDriver driver = new EdgeDriver();
        driver.manage().timeouts().implicitlyWait(Integer.parseInt(configvariable.getStringVar("web.implicit.wait")), TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }
}

