package com.prod.tap.selenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class JavaScriptExec {
    private static final Logger logger = Logger.getLogger(JavaScriptExec.class);

    public void scrollWebPage(WebDriver driver, String xCoordinate, String yCoordinate) {
        String windowScrollScript =  "window.scrollBy(" + xCoordinate + "," + yCoordinate + ")";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(windowScrollScript, new Object[0]);
    }

    public void slideHorizontallyUsingJavaScript(WebDriver driver, String locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.xpath(locator));
        js.executeScript("arguments[0].setAttribute('style', 'left: 30%;')", element);
    }

    public void selectMenuElement(WebDriver driver, String locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("var x = $(\'" + locator + "\');");
        stringBuilder.append("x.click();");
        js.executeScript(stringBuilder.toString());
    }

    public void clickElementJS(WebDriver driver, String locator) {
        WebElement element = driver.findElement(By.xpath(locator));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }

    public void scrollPage(WebDriver driver) {
        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("window.scrollBy(0,document.body.scrollHeight)");
    }

    public void setTextElementJS(WebDriver driver, String elementID, String value) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById(elementID).value=value;");
    }

    public void rightClickElement(WebDriver driver, String locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.xpath(locator));
        String javaScript = "var evt = document.createEvent('MouseEvents');"
                + "var RIGHT_CLICK_BUTTON_CODE = 2;"
                + "evt.initMouseEvent('contextmenu', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, RIGHT_CLICK_BUTTON_CODE, null);"
                + "arguments[0].dispatchEvent(evt)";

        js.executeScript(javaScript, element);
    }

    public void scrollWebPageTillElementVisible(WebDriver driver, String locator) {
        WebElement element = driver.findElement(By.xpath(locator));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //This will scroll the page till the element is found
        js.executeScript("arguments[0].scrollIntoView();", element);
    }

    public void scrollPageUP(WebDriver driver) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(0, -document.body.scrollHeight)", new Object[0]);
    }
}
