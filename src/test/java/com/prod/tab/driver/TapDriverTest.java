package com.prod.tab.driver;

import com.prod.tap.driver.TapDriver;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class TapDriverTest {

    @Spy
    private TapDriver mockCreateSession = new TapDriver();

    @Mock
    private WebDriver mockDriver;


    @BeforeTest
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockCreateSession.driver = mockDriver;
    }

//    @Test
//    public void testCreateIOSDriver() throws Exception {
//        String osName = "iOS";
//        String appPath = "";
//        doNothing().when(mockCreateSession).iOSDriver(osName);
//        mockCreateSession.createDriver(osName, appPath);
//        verify(mockCreateSession, times(1)).iOSDriver(osName);
//    }
//
//    @Test
//    public void testCreateAndroidDriver() throws Exception {
//        String osName = "Android";
//        String appPath = "";
//        doNothing().when(mockCreateSession).androidDriver(osName);
//        mockCreateSession.createDriver(osName, appPath);
//        verify(mockCreateSession, atLeastOnce()).androidDriver(osName);
//    }

    @Test
    public void testAndroidDriver() throws Exception {
        String osName = "Android";
        doNothing().when(mockCreateSession).androidDriver(osName, "", "");
        mockCreateSession.androidDriver(osName, "", "");
        verify(mockCreateSession, times(1)).androidDriver(osName, "", "");
    }

    @Test
    public void testiOSDriver() throws Exception {
        String osName = "iOS";
        doNothing().when(mockCreateSession).iOSDriver(osName, "", "", "");
        mockCreateSession.iOSDriver(osName, "", "", "");
        reset();
        verify(mockCreateSession, atLeastOnce()).iOSDriver(osName, "", "", "");
    }

    @Test
    public void testChromeBrowserDriver() throws Exception {
        doReturn(mockDriver).when(mockCreateSession).chromeBrowserDriver();
        mockCreateSession.chromeBrowserDriver();
        verify(mockCreateSession, times(1)).chromeBrowserDriver();
    }

    @Test
    public void testCreateHTMLUnitDriver() throws Exception {
        doReturn(mockDriver).when(mockCreateSession).createHTMLUnitDriver("www.google.com");
        mockCreateSession.createHTMLUnitDriver("www.google.com");
        verify(mockCreateSession, times(1)).createHTMLUnitDriver("www.google.com");
    }

    @Test
    public void testCreatePhantomJsDriver() throws Exception {
        doReturn(mockDriver).when(mockCreateSession).createPhantomJsDriver();
        mockCreateSession.createPhantomJsDriver();
        verify(mockCreateSession, times(1)).createPhantomJsDriver();
    }

    @Test
    public void testCreateChromeDriver() throws Exception {
        doReturn(mockDriver).when(mockCreateSession).createChromeDriver();
        mockCreateSession.createChromeDriver();
        verify(mockCreateSession, times(1)).createChromeDriver();
    }


    @Test
    public void testGetWebDriver() throws Exception {
        doReturn(mockDriver).when(mockCreateSession).getWebDriver();
        assertEquals(mockDriver, mockCreateSession.getWebDriver());
        verify(mockCreateSession, times(1)).getWebDriver();
    }

    @Test
    public void testinvokeAppium() throws Exception {
        // doNothing().when(mockCreateSession).startAppiumServer("mac");
        mockCreateSession.invokeAppium();
        verify(mockCreateSession, times(1)).invokeAppium();
    }

    @Test
    public void testStopAppium() throws Exception {
        // doNothing().when(mockCreateSession).stopAppiumServer("mac os x");
        mockCreateSession.stopAppium();
        verify(mockCreateSession, times(1)).stopAppium();
    }

    @Test
    public void testTeardown() throws Exception {
        // mockCreateSession.driver = mockDriver;
        mockCreateSession.teardown();
        verify(mockCreateSession, atLeastOnce()).teardown();
    }

    @Test
    public void testStartAppiumServer() throws Exception {
        mockCreateSession.startAppiumServer("mac");
        verify(mockCreateSession, atLeastOnce()).startAppiumServer("mac");
    }

}
