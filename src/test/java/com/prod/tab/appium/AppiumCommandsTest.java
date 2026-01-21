package com.prod.tab.appium;

import com.prod.tap.appium.AppiumCommands;
import io.appium.java_client.MobileElement;
import org.mockito.*;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;


public class AppiumCommandsTest {


    @Spy
    @InjectMocks
    private AppiumCommands mockGen = new AppiumCommands();

    @Mock
    private WebDriver mockDriver;

    @Mock
    private WebElement mockElement;

    @Mock
    private By mockBy;

    @BeforeTest
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testisElementPresent() {
        doReturn(true).when(mockGen).waitForVisibility(mockBy);
        Assert.assertTrue(mockGen.isElementPresent(mockBy));
    }

    @Test
    public void testisAlertPresent() {
        WebDriver.TargetLocator mockTargetLocator = mock(WebDriver.TargetLocator.class);
        when(mockDriver.switchTo()).thenReturn(mockTargetLocator);
        Assert.assertTrue(mockGen.isAlertPresent());
    }

    @Test
    public void testlaunchApp() {
        doNothing().when(mockGen).launchApp();
        mockGen.launchApp();
        verify(mockGen, atLeast(1)).launchApp();
    }

    @Test
    public void testisElementDisplayed() {
        when(mockDriver.findElement(mockBy)).thenReturn(mockElement);
        when(mockElement.isDisplayed()).thenReturn(true);
        Assert.assertTrue(mockGen.isElementDisplayed(mockBy));
    }

    @Test
    public void testHideKeyboard() {
        doNothing().when(mockGen).hideKeyboard();
        mockGen.hideKeyboard();
        verify(mockGen, atLeast(1)).hideKeyboard();
    }

    @Test
    public void testSelectElementPicker() {

        doReturn(true).when(mockGen).isElementPresent(ArgumentMatchers.anyObject());
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        mockGen.selectElementPicker(1, "test");
        verify(mockGen, atLeast(1)).isElementPresent(ArgumentMatchers.anyObject());
    }

    @Test
    public void testTap() {
        doNothing().when(mockGen).tap(101, 102);
        mockGen.tap(101, 102);
        verify(mockGen, atLeast(1)).tap(101, 102);
    }

    @Test
    public void testBack() {
        doNothing().when(mockGen).back();
        mockGen.back();
        verify(mockGen, atLeast(1)).back();
    }

    @Test
    public void testwaitForVisibility() {
        doReturn(true).when(mockGen).waitForVisibility(mockBy);
        mockGen.waitForVisibility(mockBy);
        boolean actualResult = mockGen.waitForVisibility(mockBy);
        Assert.assertTrue(actualResult);
    }

    @Test
    public void testIsEnabledTrue() {
        MobileElement mockMobileElement = mock(MobileElement.class);
        doReturn(mockMobileElement).when(mockDriver).findElement(ArgumentMatchers.anyObject());
        doReturn(true).when(mockMobileElement).isEnabled();
        Assert.assertTrue(mockGen.isEnabled(mockBy));
    }

    @Test
    public void testIsEnabledFalse() {
        MobileElement mockMobileElement = mock(MobileElement.class);
        doReturn(mockMobileElement).when(mockDriver).findElement(ArgumentMatchers.anyObject());
        doReturn(false).when(mockMobileElement).isEnabled();
        Assert.assertFalse(mockGen.isEnabled(mockBy));
    }

    @Test
    public void testFindElement() {
        doReturn(true).when(mockGen).waitForVisibility(ArgumentMatchers.anyObject());
        doReturn(mockElement).when(mockDriver).findElement(ArgumentMatchers.anyObject());
        WebElement actualElement = mockGen.findElement(mockBy);
        Assert.assertEquals(actualElement, mockElement);
    }

    @Test
    public void testGetAlertText() {
        WebDriver.TargetLocator mockTargetLocator = mock(WebDriver.TargetLocator.class);
        Alert mockAlert = mock(Alert.class);
        when(mockDriver.switchTo()).thenReturn(mockTargetLocator);
        doReturn(mockAlert).when(mockTargetLocator).alert();
        doReturn("mocktext").when(mockAlert).getText();
        String actualText = mockGen.getAlertText();
        Assert.assertEquals(actualText, "mocktext");
    }

    @Test
    public void testAcceptAlert() {
        WebDriver.TargetLocator mockTargetLocator = mock(WebDriver.TargetLocator.class);
        Alert mockAlert = mock(Alert.class);
        when(mockDriver.switchTo()).thenReturn(mockTargetLocator);
        doReturn(mockAlert).when(mockTargetLocator).alert();
        doNothing().when(mockAlert).accept();
        mockGen.acceptAlert();
        verify(mockTargetLocator, atLeast(1)).alert();
        verify(mockDriver, atLeast(1)).switchTo();
    }

    @Test
    public void testDismissAlert() {
        WebDriver.TargetLocator mockTargetLocator = mock(WebDriver.TargetLocator.class);
        Alert mockAlert = mock(Alert.class);
        when(mockDriver.switchTo()).thenReturn(mockTargetLocator);
        doReturn(mockAlert).when(mockTargetLocator).alert();
        doNothing().when(mockAlert).dismiss();
        mockGen.dismissAlert();
        verify(mockTargetLocator, atLeast(1)).alert();
        verify(mockDriver, atLeast(1)).switchTo();
    }

    @Test
    public void testGetNetworkConnection() {
        doNothing().when(mockGen).getNetworkConnection();
        mockGen.getNetworkConnection();
        verify(mockGen, atLeast(1)).getNetworkConnection();
    }

    @Test
    public void testOpenNotification() {
        doNothing().when(mockGen).openNotifications();
        mockGen.openNotifications();
        verify(mockGen, atLeast(1)).openNotifications();
    }

    @Test
    public void testClick() {
        String elementByName = "test";
        doNothing().when(mockGen).click(elementByName);
        mockGen.click(elementByName);
        verify(mockGen, atLeast(1)).click(elementByName);
    }

    @Test
    public void testSelectDatePicker() {
        doReturn(true).when(mockGen).isElementPresent(ArgumentMatchers.anyObject());
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        mockGen.selectDatePicker(1, "test");
        verify(mockGen, atLeast(1)).isElementPresent(ArgumentMatchers.anyObject());
        verify(mockGen, atLeast(1)).findElementNoWait(ArgumentMatchers.anyObject());
    }

    @Test
    public void testAcceptWheeler() {
        doReturn(true).when(mockGen).isElementPresent(ArgumentMatchers.anyObject());
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        doNothing().when(mockElement).click();
        mockGen.acceptWheeler();
        verify(mockGen, atLeast(1)).isElementPresent(ArgumentMatchers.anyObject());
        verify(mockGen, atLeast(1)).findElementNoWait(ArgumentMatchers.anyObject());
    }

    @Test
    public void testClearText() {
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        doNothing().when(mockElement).clear();
        mockGen.cleartext(ArgumentMatchers.anyObject());
        verify(mockGen, atLeast(1)).findElementNoWait(ArgumentMatchers.anyObject());
        verify(mockElement, atLeast(1)).clear();
    }

    @Test
    public void testClearAndEnterText() {
        String mockData = "test";
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        doNothing().when(mockElement).clear();
        doNothing().when(mockElement).sendKeys(mockData);
        mockGen.clearAndEnterText(mockBy, mockData);
        verify(mockGen, atLeast(1)).findElement(ArgumentMatchers.anyObject());
        verify(mockElement, atLeast(1)).clear();
        verify(mockElement, atLeast(1)).sendKeys(mockData);
    }

    @Test
    public void testGetTextFromField() {
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        doReturn("test").when(mockElement).getAttribute("value");
        String actualText = mockGen.getTextFromField(mockBy);
        Assert.assertEquals(actualText, "test");
    }

    @Test
    public void testGetTextOfElement() {
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        doReturn("test").when(mockElement).getText();
        String actualText = mockGen.getTextofElement(mockBy);
        Assert.assertEquals(actualText, "test");
    }

    @Test
    public void testClickElementByCoordinate() {
        Point mockPoint = mock(Point.class);
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        doReturn(mockPoint).when(mockElement).getLocation();
        double xcord = mockPoint.getX();
        double ycord = mockPoint.getY();
        int offset = 100;
        doNothing().when(mockGen).tap(xcord, ycord + offset);
        mockGen.clickElementByCoordinate(mockBy, offset);
        verify(mockGen, atLeast(1)).tap(xcord, ycord + offset);
    }

    @Test
    public void testClickElementByCoordinateForBothXY() {
        Point mockPoint = mock(Point.class);
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        doReturn(mockPoint).when(mockElement).getLocation();
        double xcord = mockPoint.getX();
        double ycord = mockPoint.getY();
        int xOffset = 100;
        int yOffset = 150;
        doNothing().when(mockGen).tap(xcord + xOffset, ycord + yOffset);
        mockGen.clickElementByCoordinateforBothXY(mockBy, xOffset, yOffset);
        verify(mockGen, atLeast(1)).tap(xcord + xOffset, ycord + yOffset);
    }

    @Test
    public void testClickElementByCoordinatewithOffset() {
        int offset = 100;
        Dimension mockDimension = mock(Dimension.class);
        doReturn(mockDimension).when(mockGen).getScreenDimension();
        double y = (mockDimension.height / 2) + offset;
        double x = mockDimension.width / 2;
        doNothing().when(mockGen).tap(x, y);
        mockGen.clickElementByCoordinate(offset);
        verify(mockGen, atLeast(1)).getScreenDimension();
        verify(mockGen, atLeast(1)).tap(x, y);
    }

    @Test
    public void testGetScreenDimension() {
        Dimension mockDimension = mock(Dimension.class);
        WebDriver.Options mockOptions = mock(WebDriver.Options.class);
        WebDriver.Window mockWindow = mock(WebDriver.Window.class);
        doReturn(mockOptions).when(mockDriver).manage();
        doReturn(mockWindow).when(mockOptions).window();
        doReturn(mockDimension).when(mockWindow).getSize();
        mockGen.getScreenDimension();
        Assert.assertNotNull(mockDimension);
    }

    @Test
    public void testClickOnCameraUsingCoordinateForIOS() {
        doNothing().when(mockGen).clickOnCameraUsingCoordinateForIOS();
        mockGen.clickOnCameraUsingCoordinateForIOS();
        verify(mockGen, atLeast(1)).clickOnCameraUsingCoordinateForIOS();
    }

    @Test
    public void testHandlePhotoPermission() {
        String option = "Camera";
        doReturn(true).when(mockGen).isAlertPresent();
        doNothing().when(mockGen).acceptAlert();
        doReturn(true).when(mockGen).isElementPresent(ArgumentMatchers.anyObject());
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        doNothing().when(mockElement).click();
        doNothing().when(mockGen).reactivateIOSApp("com.prudential.pulse.sg");
        mockGen.handlePhotoPermission(option);
        verify(mockGen, atLeast(1)).isElementPresent(ArgumentMatchers.anyObject());
        verify(mockGen, atLeast(1)).findElementNoWait(ArgumentMatchers.anyObject());
        verify(mockGen, atLeast(1)).reactivateIOSApp("com.prudential.pulse.sg");
    }

    @Test
    public void testreactivateIOSApp() {
        doNothing().when(mockGen).reactivateIOSApp("com.prudential.pulse.sg");
        mockGen.reactivateIOSApp("com.prudential.pulse.sg");
        verify(mockGen, atLeast(1)).reactivateIOSApp("com.prudential.pulse.sg");
    }

    @Test
    public void testTakePictureFromIOSCamera() {
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        mockGen.takePictureFromIOSCamera();
        verify(mockGen, atLeast(1)).findElement(ArgumentMatchers.anyObject());
    }

    @Test
    public void testPressIOSKeyBoardKey() {
        String key = "Return";
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        doNothing().when(mockElement).click();
        mockGen.pressIOSKeyBoardKey(key);
        verify(mockGen, atLeast(1)).findElementNoWait(ArgumentMatchers.anyObject());
    }

    @Test
    public void acceptAlertWithWait() {
        doNothing().when(mockGen).acceptAlertWithWait(5000);
        mockGen.acceptAlertWithWait(5000);
        verify(mockGen, atLeast(1)).acceptAlertWithWait(5000);
    }

    @Test
    public void testHideIOSKeyBoardUsingKeyboardLocation() {
        doNothing().when(mockGen).hideIOSKeyBoardUsingKeyboardLocation();
        mockGen.hideIOSKeyBoardUsingKeyboardLocation();
        verify(mockGen, atLeast(1)).hideIOSKeyBoardUsingKeyboardLocation();
    }

    @Test
    public void testIsKeyBoardKeyPresent() {
        String key = "Return";
        doReturn(true).when(mockGen).isElementPresent(ArgumentMatchers.anyObject());
        doNothing().when(mockGen).pressIOSKeyBoardKey(key);
        mockGen.isKeyBoardKeyPresent(key);
        verify(mockGen, atLeast(1)).isElementPresent(ArgumentMatchers.anyObject());
        verify(mockGen, atLeast(1)).pressIOSKeyBoardKey(key);
    }

    @Test
    public void testIsElementDisplayed() {
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        doReturn(true).when(mockElement).isDisplayed();
        Assert.assertTrue(mockGen.isElementDisplayed(mockBy));
    }

    @Test
    public void testVerifyTextEachWebElement() {
        List<By> byList = new ArrayList<>();
        byList.add(mockBy);
        doReturn(true).when(mockGen).isElementPresent(ArgumentMatchers.anyObject());
        mockGen.verifyTextEachWebElement(byList);
        verify(mockGen, atLeast(1)).isElementPresent(ArgumentMatchers.anyObject());
    }

    @Test
    public void testRunABDShellCommand() {
        String command = "test";
        doNothing().when(mockGen).runADBShellCommand(command);
        mockGen.runADBShellCommand(command);
        verify(mockGen, atLeast(1)).runADBShellCommand(command);
    }

    @Test
    public void testSelectAndroidElementSpinner() {
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        doNothing().when(mockElement).click();
        mockGen.selectAndroidElementSpinner();
        verify(mockGen, atLeast(1)).findElement(ArgumentMatchers.anyObject());
        verify(mockElement, atLeast(1)).click();
    }

    @Test
    public void testTakePictureFromAndroidCamera() {
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        mockGen.takePictureFromAndroidCamera();
        verify(mockGen, atLeast(2)).findElement(ArgumentMatchers.anyObject());
    }

    @Test
    public void testGetTextFromFieldUsingAttributeName() {
        String attributeName = "value";
        doReturn(mockElement).when(mockGen).findElement(ArgumentMatchers.anyObject());
        doReturn("test").when(mockElement).getAttribute(attributeName);
        String actualValue = mockGen.getTextFromField(attributeName, mockBy);
        Assert.assertEquals(actualValue, "test");
    }

    @Test
    public void testWaitForTime() {
        int time = 5;
        doNothing().when(mockGen).waitForTime(time);
        mockGen.waitForTime(time);
        verify(mockGen, atLeast(1)).waitForTime(time);
    }

    @Test
    public void testClickElement() {
        doReturn(true).when(mockGen).waitForClickable(mockBy);
        doReturn(mockElement).when(mockGen).findElementNoWait(ArgumentMatchers.anyObject());
        doNothing().when(mockElement).click();
        mockGen.clickElement(mockBy);
        verify(mockElement, atLeast(1)).click();
        verify(mockGen, atLeast(1)).findElementNoWait(ArgumentMatchers.anyObject());
        verify(mockGen, atLeast(1)).waitForClickable(mockBy);
    }


}

