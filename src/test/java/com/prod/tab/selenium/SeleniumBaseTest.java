package com.prod.tab.selenium;//package com.prudential.tap.selenium;
//
//import com.prudential.tap.config.Configvariable;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.testng.Assert;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//@ComponentScan(basePackages = {"com.prudential.tap"})
//@Configuration
//public class SeleniumBaseTest {
//
//    public static ConfigurableApplicationContext context;
//
//
//    private SeleniumBase seleniumBase;
//    private ExecuteCommand executeCommand;
//    private Configvariable configvariable;
//
//    @BeforeClass
//    public void setup() {
//        if (context == null) {
//            context = new AnnotationConfigApplicationContext(SeleniumBaseTest.class);
//            seleniumBase = context.getBean(SeleniumBase.class);
//            executeCommand = context.getBean(ExecuteCommand.class);
//            configvariable = context.getBean(Configvariable.class);
//        }
//    }
//
//    @Test
//    public void launchAppWithUsernameAndPass() {
//        seleniumBase.initializeSeleniumFramework();
//        seleniumBase.launchApplication("http://the-internet.herokuapp.com/basic_auth", "admin", "admin");
//        seleniumBase.quitDriver();
//    }
//
//
//    @Test
//    public void launchAppWithoutUsernameAndPass() {
//        seleniumBase.initializeSeleniumFramework();
//        seleniumBase.launchApplication("http://the-internet.herokuapp.com/basic_auth", "", "");
//        seleniumBase.quitDriver();
//    }
//
//    @Test
//    public void launchAppWithoutUsernameAndPassInIE() {
//        if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
//            System.setProperty("web.browser.type", "ie");
//            seleniumBase.initializeSeleniumFramework();
//            seleniumBase.launchApplication("http://www.google.com", "", "");
//            seleniumBase.quitDriver();
//        }
//    }
//
//    @Test
//    public void launchAppWithoutUsernameAndPassInFirefox() {
//        System.setProperty("web.browser.type", "firefox");
//        seleniumBase.initializeSeleniumFramework();
//        seleniumBase.launchApplication("http://the-internet.herokuapp.com/basic_auth", "admin", "admin");
//        seleniumBase.quitDriver();
//    }
//
//    @Test
//    public void launchAppWithoutUsernameAndPassInPhantomJS() {
//        System.setProperty("web.browser.type", "phantom");
//        seleniumBase.initializeSeleniumFramework();
//        seleniumBase.launchApplication("http://the-internet.herokuapp.com/basic_auth", "admin", "admin");
//        seleniumBase.quitDriver();
//    }
//
//    @Test
//    public void launchAppWithoutUsernameAndPassInEdge() {
//        System.setProperty("web.browser.type", "edge");
//        seleniumBase.initializeSeleniumFramework();
//        seleniumBase.launchApplication("http://the-internet.herokuapp.com/", "", "");
//        seleniumBase.quitDriver();
//    }
//
//    @Test
//    public void selectDropDown() {
//        System.setProperty("web.browser.type", "chrome");
//        seleniumBase.initializeSeleniumFramework();
//        seleniumBase.launchApplication("http://the-internet.herokuapp.com", "", "");
//        executeCommand.executeCommand(SeleniumBase.driver, Commands.WAIT_FOR_ELEMENT_TO_PRESENT, null, "//a[text()=\"Dropdown\"]", "20");
//        executeCommand.executeCommand(SeleniumBase.driver, Commands.CLICK, null, "//a[text()=\"Dropdown\"]", "");
//        executeCommand.executeCommand(SeleniumBase.driver, Commands.SELECT_BY_VISIBLE_TEXT, null, "//select[@id=\"dropdown\"]", "Option 2");
//        executeCommand.executeCommand(SeleniumBase.driver, Commands.GET_SELECTED_TEXT, null, "//select[@id=\"dropdown\"]", "");
//        String text = configvariable.getStringVar("selected.dropdown.value");
//        Assert.assertTrue(!text.isEmpty());
//        seleniumBase.quitDriver();
//    }
//
//}
