package com.prod.tap.appium;

import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;


@Component
public class AppiumCommands {
    private static final Logger LOGGER = Logger.getLogger(AppiumCommands.class);

    // common timeout for all tests can be set here
    public final int timeOut = 20;

    private WebDriver driver;

    private MobileDriver mobiDriver;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }


    /**
     * method verify whether element is present on screen
     *
     * @param targetElement element to be present
     * @return true if element is present else throws exception
     * @throws InterruptedException Thrown when a thread is waiting, sleeping,
     *                              or otherwise occupied, and the thread is interrupted, either before
     *                              or during the activity.
     */
    public Boolean isElementPresent(By targetElement) {
        return waitForVisibility(targetElement);
    }

    /**
     * method to hide keyboard
     */
    public void hideKeyboard() {
        ((AppiumDriver) driver).hideKeyboard();
    }

    /**
     * method to go back by Android Native back click
     */
    public void back() {
        ((AndroidDriver) driver).pressKey(new KeyEvent().withKey(AndroidKey.BACK));
    }

    /**
     * method to wait for an element to be visible
     *
     * @param targetElement element to be visible
     * @return true if element is visible else throws TimeoutException
     */
    public boolean waitForVisibility(By targetElement) {
        try {
            Wait wait = new FluentWait(driver)
                    .withTimeout(Duration.ofSeconds(timeOut))
                    .pollingEvery(Duration.ofSeconds(1))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);
//            WebDriverWait wait = new WebDriverWait(driver, timeOut);
            wait.until(ExpectedConditions.visibilityOfElementLocated(targetElement));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * method to check an element is currently enabled
     *
     * @param locator element to be found
     * @return true or false
     */
    public boolean isEnabled(By locator) {
        boolean isEnabled = false;
        try {
            MobileElement element = driver.findElement(locator);
            isEnabled = element.isEnabled();

        } catch (Exception e) {
            LOGGER.warn("Element is not enabled " + locator);
        }
        return isEnabled;
    }

    /**
     * method to tap on the screen on provided coordinates
     *
     * @param xPosition x coordinate to be tapped
     * @param yPosition y coordinate to be tapped
     */
    public void tap(double xPosition, double yPosition) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        HashMap<String, Double> tapObject = new HashMap<String, Double>();
        tapObject.put("x", xPosition);
        tapObject.put("y", yPosition);
        js.executeScript("mobile: tap", tapObject);
    }


    /**
     * method to find an element
     *
     * @param locator element to be found
     * @return WebElement if found else throws NoSuchElementException
     */
    public WebElement findElement(By locator) {
        WebElement element;
        try {
            waitForVisibility(locator);
            element = driver.findElement(locator);
            return element;
        } catch (Exception e) {
            LOGGER.warn("Element not found " + locator);
            throw new TapException(TapExceptionType.EXPECTED_WEBELEMENT_DOESNOT_EXIST, "Not able to find element on screen [{}]", locator.toString());
        }
    }

    public WebElement findElementNoWait(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element;
        } catch (Exception e) {
            LOGGER.warn("Element not found " + locator);
            throw new TapException(TapExceptionType.EXPECTED_WEBELEMENT_DOESNOT_EXIST, "Not able to find element on screen [{}]", locator.toString());
        }
    }

    /**
     * method to find all the elements of specific locator
     *
     * @param locator element to be found
     * @return return the list of elements if found else throws NoSuchElementException
     */
    public List<WebElement> findElements(By locator) {
        try {
            List<WebElement> element = driver.findElements(locator);
            return element;
        } catch (Exception e) {
            LOGGER.error("Element not found" + locator);
            throw new TapException(TapExceptionType.EXPECTED_WEBELEMENT_DOESNOT_EXIST, "Not able to find element on screen [{}]", locator.toString());
        }
    }

    /**
     * method to get message test of alert
     *
     * @return message text which is displayed
     */
    public String getAlertText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            return alertText;
        } catch (Exception e) {
            LOGGER.error("Alert not present");
            throw new TapException(TapExceptionType.EXPECTED_ALERT_DOESNOT_EXIST, "Alert is not present to get text");
        }
    }

    /**
     * method to verify if alert is present
     *
     * @return returns true if alert is present else false
     */
    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    /**
     * method to Accept Alert if alert is present
     */

    public void acceptAlert() {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    /**
     * method to Dismiss Alert if alert is present
     */

    public void dismissAlert() {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
    }

    /**
     * method to get network settings
     */
    public void getNetworkConnection() {
        LOGGER.info(((AndroidDriver) driver).getConnection());
    }


    /**
     * method to set network settings
     *
     * @param airplaneMode pass true to activate airplane mode else false
     * @param wifi         pass true to activate wifi mode else false
     * @param data         pass true to activate data mode else false
     */
//    public void setNetworkConnection(boolean airplaneMode, boolean wifi, boolean data) {
//
//        long mode = 1L;
//
//        if (wifi) {
//            mode = 2L;
//        } else if (data) {
//            mode = 4L;
//        }
//
//        ConnectionState connectionState = new ConnectionState(mode);
//        ((AndroidDriver) driver).setConnection(connectionState);
//        LOGGER.info("Your current connection settings are :" + ((AndroidDriver) driver).getConnection());
//    }


    /**
     * method to set the context to required view.
     *
     * @param context view to be set
     */
    public void setContext(String context) {

        Set<String> contextNames = ((AppiumDriver) driver).getContextHandles();

        if (contextNames.contains(context)) {
            ((AppiumDriver) driver).context(context);
            LOGGER.info("Context changed successfully");
        } else {
            LOGGER.info(context + "not found on this page");
        }

        LOGGER.warn("Current context" + ((AppiumDriver) driver).getContext());
    }

    /**
     * method to long press on specific element by passing locator
     *
     * @param locator element to be long pressed
     */
//    public void longPress(By locator) {
//        try {
//            WebElement element = driver.findElement(locator);
//            TouchAction touch = new TouchAction((MobileDriver) driver);
//            LongPressOptions longPressOptions = new LongPressOptions();
//            longPressOptions.withElement(ElementOption.element(element));
//            touch.longPress(longPressOptions).release().perform();
//            LOGGER.info("Long press successful on element: " + element);
//        } catch (Exception e) {
//            LOGGER.error("Element not found " + locator);
//            throw new PulseException(PulseExceptionType.EXPECTED_WEBELEMENT_DOESNOT_EXIST, "Not able to find element on screen [{}]", locator.toString());
//        }
//    }

    /**
     * method to long press on specific x,y coordinates
     *
     * @param x x offset
     * @param y y offset
     */
//    public void longPress(int x, int y) {
//        TouchAction touch = new TouchAction((MobileDriver) driver);
//        PointOption pointOption = new PointOption();
//        pointOption.withCoordinates(x, y);
//        touch.longPress(pointOption).release().perform();
//        LOGGER.info("Long press successful on coordinates: " + "( " + x + "," + y + " )");
//    }

    /**
     * method to long press on element with absolute coordinates.
     *
     * @param locator element to be long pressed
     * @param x       x offset
     * @param y       y offset
     */
//    public void longPress(By locator, int x, int y) {
//        try {
//            WebElement element = driver.findElement(locator);
//            TouchAction touch = new TouchAction((MobileDriver) driver);
//            LongPressOptions longPressOptions = new LongPressOptions();
//            longPressOptions.withPosition(new PointOption().withCoordinates(x, y)).withElement(ElementOption.element(element));
//            touch.longPress(longPressOptions).release().perform();
//            LOGGER.info("Long press successful on element: " + element + "on coordinates" + "( " + x + "," + y + " )");
//        } catch (NoSuchElementException e) {
//            LOGGER.error("Element not found " + locator);
//            throw new PulseException(PulseExceptionType.EXPECTED_WEBELEMENT_DOESNOT_EXIST, "Not able to find element on screen [{}]", locator.toString());
//        }
//
//    }


//    public static String uiScrollable(String uiSelector) {
//        return "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(" + uiSelector + ".instance(0));";
//    }

    /**
     * method to open notifications on Android
     */

    public void openNotifications() {
        ((AndroidDriver) driver).openNotifications();
    }

    /**
     * method to launchApp
     */

    public void launchApp() {
        ((AppiumDriver) driver).launchApp();
    }


    /**
     * method to click on Element By Name
     *
     * @param elementByName - String element name to be clicked
     */

    public void click(String elementByName) {
        ((AppiumDriver) driver).findElementByName(elementByName).click();
    }


    public void selectElementPicker(int wheelerIndex, String name) {
        By elementPicker = By.xpath("//XCUIElementTypePicker//XCUIElementTypePickerWheel[" + wheelerIndex + "]");
        if (isElementPresent(elementPicker)) {
            findElementNoWait(elementPicker).sendKeys(name);
        }
    }

    public void selectDatePicker(int pickerWheelIndex, String selectValue) {
        By datePicker = By.xpath("//XCUIElementTypeDatePicker//XCUIElementTypePickerWheel[" + pickerWheelIndex + "]");
        if (isElementPresent(datePicker)) {
            findElementNoWait(datePicker).sendKeys(selectValue);
        }
    }

    public void acceptWheeler() {
        By doneButton = By.xpath("(//XCUIElementTypeOther[@name='Done'])[2]");
        if (isElementPresent(doneButton)) {
            findElementNoWait(doneButton).click();
        }
    }

    public void cleartext(By locator) {
        WebElement textField = findElementNoWait(locator);
        textField.clear();
    }

    public void clearAndEnterText(By locator, String value) {
        WebElement textField = findElement(locator);
        textField.clear();
        textField.sendKeys(value);
    }

    public String getTextFromField(By locator) {
        WebElement textField = findElement(locator);
        return textField.getAttribute("value").toString();
    }

    public String getTextofElement(By locator) {
        return findElement(locator).getText();
    }

    public enum DIRECTION {LEFT, RIGHT, UP, DOWN}

    ;

    public void swipe(DIRECTION direction, long duration) {

        mobiDriver = (MobileDriver) driver;
        Dimension size = mobiDriver.manage().window().getSize();

        int startX;
        int endX = 0;
        int startY;
        int endY = 0;

        switch (direction) {
            case RIGHT:
                new TouchAction(mobiDriver)
                        .press(PointOption.point(427, 878))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
                        .moveTo(PointOption.point(427, 554))
                        .release().
                        perform();

                break;

            case LEFT:
                startY = (size.height / 2);
                startX = (int) (size.width * 0.05);
                endX = (int) (size.width * 0.90);
                new TouchAction(mobiDriver)
                        .press(PointOption.point(startX, startY))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
                        .moveTo(PointOption.point(endX, endY))
                        .release()
                        .perform();

                break;

            case UP:
                endY = (int) (size.height * 0.90);
                startY = (int) (size.height * 0.20);
                startX = (size.width / 2);
                new TouchAction(mobiDriver)
                        .press(PointOption.point(startX, startY))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
                        .moveTo(PointOption.point(endX, endY))
                        .release()
                        .perform();
                break;


            case DOWN:
                startY = (int) (size.height * 0.80);
                endY = (int) (size.height * 0.10);
                startX = (size.width / 2);
                new TouchAction(mobiDriver)
                        .press(PointOption.point(startX, startY))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
                        .moveTo(PointOption.point(endX, endY))
                        .release()
                        .perform();

                break;
            default:

        }
    }

    public void hideIOSKeyBoard() {
        Dimension size = getScreenDimension();
        int height = size.getHeight();
        int width = size.getWidth();
        new TouchAction(((MobileDriver) driver))
                .press(PointOption.point(width, height))
                .release().perform();

    }

    public void clickElementByCoordinate(By locator, int offset) {
        WebElement element = findElementNoWait(locator);
        Point classname = element.getLocation();
        double xcord = classname.getX();
        double ycord = classname.getY();
        tap(xcord, ycord + offset);
    }

    public void clickElementByCoordinateforBothXY(By locator, int xOffset, int yOffset) {
        WebElement element = findElementNoWait(locator);
        Point classname = element.getLocation();
        double xcord = classname.getX();
        double ycord = classname.getY();
        tap(xcord + xOffset, ycord + yOffset);
    }

    public Dimension getScreenDimension() {
        return driver.manage().window().getSize();
    }

    public void clickOnCameraUsingCoordinateForIOS() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Thread.sleep failed");
        }
        Dimension size = getScreenDimension();
        int height = size.getHeight();
        int width = size.getWidth();
        new TouchAction(((MobileDriver) driver))
                .press(PointOption.point(width / 2 - 100, height / 2))
                .release().perform();
    }

    public void handlePhotoPermission(String option) {
        if (isAlertPresent()) {
            acceptAlert();
            if ("Photos".equalsIgnoreCase(option)) {
                By settingPhotos = By.xpath("//XCUIElementTypeCell[@name='Photos']");
                if (isElementPresent(settingPhotos)) {
                    findElementNoWait(settingPhotos).click();
                    By settingReadWrite = By.xpath("//XCUIElementTypeCell[@name='Read and Write']");
                    findElementNoWait(settingReadWrite).click();
                    reactivateIOSApp("com.prudential.pulse.sg");
                }
            } else if ("Camera".equalsIgnoreCase(option)) {
                By settingCamera = By.xpath("//XCUIElementTypeSwitch[@name='Camera']");
                if (isElementPresent(settingCamera)) {
                    findElementNoWait(settingCamera).click();
                    reactivateIOSApp("com.prudential.pulse.sg");
                }
            }
        }
    }

    public void reactivateIOSApp(String bundleId) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("bundleId", bundleId);
        ((IOSDriver) driver).executeScript("mobile: activateApp", args);
    }

    public void uploadImageToIOSSimulator() {
        By allPhotos = By.xpath("//XCUIElementTypeStaticText[@name='All Photos']");
        WebElement element = driver.findElement(allPhotos);
        element.click();

        List<WebElement> photos = driver.findElements(By.className("XCUIElementTypeImage"));
        int numPhotos = photos.size();

        File assetDir = new File(System.getProperty("user.dir"), "src/test/resources/images");
        File img = null;
        try {
            img = new File(assetDir.getCanonicalPath(), "NRIC.jpg");
            ((IOSDriver) driver).pushFile("nric.jpg", img);
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "not able to get file {}", "NRIC.jpg");
        }

        photos = driver.findElements(By.className("XCUIElementTypeImage"));
        LOGGER.info("There were " + numPhotos + " photos before, and now there are " +
                photos.size() + "!");
    }

    public void takePictureFromIOSCamera() {
        By takePicture = By.xpath("//XCUIElementTypeButton[@name='PhotoCapture']");
        By usePhoto = By.xpath("//XCUIElementTypeButton[@name='Use Photo']");
        findElement(takePicture).click();
        findElement(usePhoto).click();
    }

    public void pressIOSKeyBoardKey(String key) {
        if (key.equalsIgnoreCase("Return")) {
            findElementNoWait(By.xpath("//XCUIElementTypeKeyboard//XCUIElementTypeButton[@name='Return']")).click();
        } else if (key.equalsIgnoreCase("shift")) {
            findElementNoWait(By.xpath("//XCUIElementTypeKeyboard//XCUIElementTypeButton[@name='shift']")).click();

        } else {
            findElementNoWait(By.xpath("//XCUIElementTypeKeyboard//XCUIElementTypeKey[@name='" + key + "']")).click();
        }
    }

    public void clickElementByCoordinate(int offset) {
        Dimension size = getScreenDimension();
        double y = (size.height / 2) + offset;
        double x = size.width / 2;
        tap(x, y);
    }

    public void acceptAlertWithWait(int timeOutMillis) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutMillis);
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            LOGGER.error("Not able to accept alert");
            throw new TapException(TapExceptionType.EXPECTED_WEBELEMENT_DOESNOT_EXIST, "Not able to accept alert");
        }
    }

    public void swipeUpElementJSX(By locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Map<String, Object> params = new HashMap<>();
        params.put("direction", "down");
        params.put("element", ((RemoteWebElement) driver.findElement(locator)).getId());
        js.executeScript("mobile: swipe", params);

    }

    public boolean waitForClickable(By targetElement) {
        try {
            Wait wait = new FluentWait(driver)
                    .withTimeout(Duration.ofSeconds(timeOut))
                    .pollingEvery(Duration.ofSeconds(1))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);
            wait.until(ExpectedConditions.elementToBeClickable(targetElement));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void handleIOSKeyPress(String value) {
        char[] key = value.toCharArray();

        for (char c : key) {
            if (Character.isUpperCase(c)) {
                pressIOSKeyBoardKey("shift");
                pressIOSKeyBoardKey(String.valueOf(c));
            } else if (Character.isLowerCase(c)) {
                pressIOSKeyBoardKey(String.valueOf(c));
            } else if (Character.isLetterOrDigit(c)) {
                pressIOSKeyBoardKey("more");
                pressIOSKeyBoardKey(String.valueOf(c));
                pressIOSKeyBoardKey("more");
            } else {
                pressIOSKeyBoardKey("more");
                pressIOSKeyBoardKey(String.valueOf(c));
                pressIOSKeyBoardKey("more");
            }

        }

    }

    public void clickByLocator(By locator) {
        ((AppiumDriver) driver).findElement(locator).click();
    }

    public void swipeUpElement(By locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Map<String, Object> params = new HashMap<>();
        params.put("direction", "down");
        params.put("element", ((RemoteWebElement) driver.findElement(locator)).getId());
        js.executeScript("mobile: swipe", params);

    }

    public String getAlertTextWithWait(int timeOutMillis) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutMillis);
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            LOGGER.info(alertText);
            return alertText;
        } catch (NoAlertPresentException e) {
            LOGGER.error("Alert is not present ");
            throw new TapException(TapExceptionType.EXPECTED_ALERT_DOESNOT_EXIST, "Alert is not present");
        }

    }

    public void hideIOSKeyBoardUsingKeyboardLocation() {
        IOSElement element = (IOSElement) ((IOSDriver) driver).findElementByClassName("XCUIElementTypeKeyboard");
        Point keyBoardPoint = element.getLocation();
        int x = keyBoardPoint.getX() + 2;
        int y = keyBoardPoint.getY() - 2;
        tap(x, y);

    }

//    public Date calculateDateBasedOnYear(int year) {
//        Date date = new Date();
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.YEAR, year);
//        date = calendar.getTime();
//        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
//        format.format(date);
//        return date;
//    }


    public void isKeyBoardKeyPresent(String key) {
        By keyBoardKey = By.xpath("//XCUIElementTypeKeyboard//XCUIElementTypeButton[@name='" + key + "']");
        if (isElementPresent(keyBoardKey)) {
            pressIOSKeyBoardKey(key);
        }
    }

//    public void clickByJSX(By locator) {
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("arguments[0].click();", driver.findElement(locator));
//    }

//    public void verifyListOfElements(List<By> locatorList) {
//        for (By locator : locatorList)
//            isElementPresent(locator);
//    }


    public boolean isElementDisplayed(By locator) {
        try {
            driver.findElement(locator).isDisplayed();
            return true;
        } catch (Exception e) {
            LOGGER.warn("Element is not displayed :" + locator.toString());
            return false;
        }
    }

    public void verifyTextEachWebElement(List<By> byList) {

        for (By reportStaticOne : byList) {
            boolean flag = false;
            flag = isElementPresent(reportStaticOne);
            if (!flag) {
                LOGGER.error("Not found the text: " + reportStaticOne.toString());
                throw new TapException(TapExceptionType.EXPECTED_WEBELEMENT_DOESNOT_EXIST, "Not found the text:{}", reportStaticOne.toString());
            }
        }

    }

    public Activity storeCurrentActivity(String appPkg, String appAct) {
//        String currentyAct = ((AndroidDriver) driver).currentActivity();
        Activity act = new Activity(appPkg, appAct);
        return act;
    }

    public void reactivateAndroidApp(Activity act) {
        act.setStopApp(false);
        ((AndroidDriver) driver).startActivity(act);
    }


//    public void moveAndroidAppBackground() {
//        //((AndroidDriver) driver).launchApp();
//        ((AndroidDriver) driver).runAppInBackground(Duration.ofSeconds(70));
//    }


    public void runADBShellCommand(String command) {
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            LOGGER.error("Failed to execute ADB command");
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to execute ADB command", e);
        }
    }

    public void selectAndroidElementSpinner() {
        findElement(By.className("android.widget.Spinner")).click();
    }

//    public void androidDatePicker(String id, String selectValue) {
//        AndroidElement picker = driver.findElement(By.xpath("//android.view.View[@resource-id='com.prudential.pulse.sg:id/" + id + "']"));
//        picker.click();
//        picker.setValue(selectValue);
//    }

    public void takePictureFromAndroidCamera() {
        By takePicture = By.xpath("//*[contains(@resource-id,'shutter_button') or contains(@text,'Shutter')]");
        By usePhoto = By.xpath("//*[@content-desc='Done' or @text='OK']");
        findElement(takePicture).click();
        findElement(usePhoto).click();
    }

    public void runAppInBackground(String platform) {
        if (platform.equalsIgnoreCase("iOS")) {
            ((IOSDriver) driver).runAppInBackground(Duration.ofSeconds(-1));
        } else if (platform.equalsIgnoreCase("Android")) {
            ((AndroidDriver) driver).runAppInBackground(Duration.ofSeconds(-1));
        }
    }

    public void showNotifications() {
        manageNotifications(true);
    }

    public void hideNotifications() {
        manageNotifications(false);
    }

    private void manageNotifications(Boolean show) {
        int yMargin = 5;
        Dimension screenSize = getScreenDimension();
        int xMid = screenSize.width / 2;
        PointOption top = PointOption.point(xMid, yMargin);
        PointOption bottom = PointOption.point(xMid, screenSize.height - yMargin);

        TouchAction action = new TouchAction((MobileDriver) driver);
        if (show) {
            action.press(top);
        } else {
            action.press(bottom);
        }
        action.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(1)));
        if (show) {
            action.moveTo(bottom);
        } else {
            action.moveTo(top);
        }
        action.perform();
    }

    public void pressIOSKeyBoardButton(String buttonName) {
        findElementNoWait(By.xpath("//XCUIElementTypeKeyboard//XCUIElementTypeButton[@name='" + buttonName + "']")).click();
    }

    public void closeApp() {
        ((AppiumDriver) driver).closeApp();
    }

    public void relaunchApp() {
        closeApp();
        launchApp();

    }

//    public void showKeyboard() {
//        ((AppiumDriver) driver).getKeyboard();
//    }

    public void deleteKeyAndroid() {
        ((AndroidDriver) driver).pressKey(new KeyEvent().withKey(AndroidKey.DEL));
    }

//    public void scrollOptionUpAndroid(String elementText) {
//
//        ((AndroidDriver) driver)
//                .findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"" + elementText + "\").instance(0))");
//    }

    //swipeLeftOrRightElementUsingCoordinate

    public void scrollElement(long duration, int eleX, int eleY, int staringXOffset, int endingXOffset, int staringYOffset, int endingYOffset) {
        mobiDriver = (MobileDriver) driver;
        new TouchAction(mobiDriver)
                .press(PointOption.point(eleX + staringXOffset, eleY + staringYOffset))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
                .moveTo(PointOption.point(eleX + endingXOffset, eleY + endingYOffset))
                .release().
                perform();
    }

    public void handlePushNotificationAndrpid(String titleText) {
        ((AndroidDriver) driver).openNotifications();
        List<WebElement> notificationTitles = driver.findElements(By.id("android:id/title"));

        for (WebElement title : notificationTitles) {
            String extractTitle = title.getText();
            if (extractTitle.contains(titleText)) {
                title.click();
                LOGGER.info("Title Notification Found");
                break;
            }
        }

        ((AndroidDriver) driver).pressKeyCode(AndroidKeyCode.BACK);
    }


    public void reactivateAndroidApp(String appPkg) {
        ((AndroidDriver) driver).activateApp(appPkg);
    }

    public void setAppToForeground(String appPkg, String appAct) {
        String shell_cmd = String.format("shell am start -a android.intent.action.MAIN -n {0}/{1}", appPkg, appAct);
        runADBShellCommand(shell_cmd);
    }

//    public void pressAndroidHomeKey() {
//        ((AndroidDriver) driver).pressKey(new KeyEvent().withKey(AndroidKey.HOME));
//    }

    public WebElement getElementByText(By locator, String text) {
        waitForVisibility(locator);
        List<WebElement> elements = findElements(locator);
        WebElement findElement = null;
        for (WebElement element : elements) {
            if (element.getText().contains(text)) {
                findElement = element;
                break;
            }
        }
        return findElement;
    }

    public void clickElement(By locator) {
        waitForClickable(locator);
        findElementNoWait(locator).click();
    }

    public boolean waitForClickable(WebElement element) {
        try {
            Wait wait = new FluentWait(driver)
                    .withTimeout(Duration.ofSeconds(timeOut))
                    .pollingEvery(Duration.ofSeconds(1))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);
            wait.until(ExpectedConditions.elementToBeClickable(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForTime(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Thread.sleep failed to wait");
        }
    }

    public String getTextFromField(String attributeName, By locator) {
        WebElement textField = findElement(locator);
        return textField.getAttribute(attributeName).toString();
    }

    public void tapAndClearTextIOS(By locator) {
        WebElement element = findElementNoWait(locator);
        double x = element.getLocation().getX() + element.getSize().width - 5;
        double y = element.getLocation().getY() + ((double) element.getSize().height / 3);
        preciseTap(x, y, 0.1, 1);
        new TouchAction(((MobileDriver) driver))
                .longPress(PointOption.point((int) x, (int) y))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000)))
                .release().perform();
        selectIOSMenuItem("Select All");
        element.sendKeys(Keys.DELETE);
    }

    public void selectIOSMenuItem(String menuName) {
        By menuItemIOS = By.xpath("//XCUIElementTypeMenu/XCUIElementTypeMenuItem[@name='" + menuName + "']");
        findElementNoWait(menuItemIOS).click();
    }

    public void preciseTap(double x, double y, double duration, int touchCount) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        HashMap<String, Double> tapObject = new HashMap<String, Double>();
        tapObject.put("x", x);
        tapObject.put("y", y);
        tapObject.put("touchCount", (double) touchCount);
        tapObject.put("duration", duration);
        js.executeScript("mobile: tap", tapObject);
    }

    public void clickElementByCoordinateforBothXY(int xOffset, int yOffset) {
        Dimension size = getScreenDimension();
        double y = (size.height / 2) + yOffset;
        double x = (size.width / 2) + xOffset;
        tap(x, y);
    }

    public void relaunchAndroidApp(String appPkg, String appAct) {
        closeApp();
        Activity act = storeCurrentActivity(appPkg, appAct);
        reactivateAndroidApp(act);
    }

    public List<LogEntry> getAllDeviceLog() {
        return driver.manage().logs().get("logcat").filter(Level.ALL);
    }

    public void clearDeviceLog() {
        String command = "adb logcat -c";
        runADBShellCommand(command);
    }

    public StringBuilder executeCommandAndGetOutput(String command) {
        StringBuilder output = null;
        try {
            Process process = Runtime.getRuntime().exec(command);
            output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                LOGGER.info(command + "Command executed successfully");
            } else {
                LOGGER.error(command + "Command not executed successfully");
            }
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Failed to execute command {}", e.getMessage());
        }
        return output;
    }

    public String getConnectedDeviceIdUsingADB() {
        StringBuilder output = executeCommandAndGetOutput("adb devices");
        String[] parts = output.toString().split("attached\n")[1].split("\tdevice");
        return parts[0];
    }

    public void connectToADBShellForDevice() {
        String command = "adb -s " + getConnectedDeviceIdUsingADB() + "shell";
        runADBShellCommand(command);
        runADBShellCommand("su");
        LOGGER.info(command + "Command executed successfully");
    }

    public void copyFileFromDeviceToLocal(String folder, String fileName) {
        String cdCommand = "cd data/data/" + Configvariable.envPropertyMap.get("pulse.android.app.package") + "/" + folder + "/";
        String copyCommand = "cp " + fileName + " /sdcard";
        String adbPullCommand = "adb pull /sdcard/" + fileName + " /" + System.getProperty("user.dir");

        executeCommandAndGetOutput(cdCommand);
        executeCommandAndGetOutput(copyCommand);
        executeCommandAndGetOutput(adbPullCommand);
        LOGGER.info(adbPullCommand + "Command executed successfully");

    }

}
