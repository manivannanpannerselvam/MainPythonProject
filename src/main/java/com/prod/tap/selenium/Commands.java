package com.prod.tap.selenium;

import com.prod.tap.config.Configvariable;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Duration;
import java.util.Iterator;
import java.util.Set;

@Component
public enum Commands {
    OPEN_URL {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String url, String value) {
            driver.get(url);
//            driver.manage().window().maximize();
            return true;
        }
    },
    NAVIGATE_TO {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String url, String value) {
            driver.navigate().to(url);
            return true;
        }
    },
    NAVIGATE_FORWARD {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.navigate().forward();
            return true;
        }
    },
    NAVIGATE_BACK {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.navigate().back();
            return true;
        }
    },
    REFRESH_BROWSER {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.navigate().refresh();
            return true;
        }
    },
    GET_TITLE {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            setGlobalVariable(driver.getTitle(), "driver.title");
            return true;
        }
    },
    GET_CURRENT_URL {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            setGlobalVariable(driver.getCurrentUrl(), "driver.current.url");
            return true;
        }
    },
    GET_PAGE_SOURCE {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            setGlobalVariable(driver.getPageSource(), "driver.page.source");
            return true;
        }
    },
    CLOSE_BROWSER {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.close();
            return true;
        }
    },
    CLOSE_ALL_BROWSER {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.quit();
            return true;
        }
    },
    WAIT_FOR_ELEMENT_TO_PRESENT {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String timeout) {
            try {
                int derivedTimeoutSeconds = Integer.parseInt(timeout) == 0 ? 10 : Integer.parseInt(timeout);
                Wait wait = new FluentWait(driver)
                        .withTimeout(Duration.ofSeconds(derivedTimeoutSeconds))
                        .pollingEvery(Duration.ofSeconds(1))
                        .ignoring(NoSuchElementException.class)
                        .ignoring(StaleElementReferenceException.class);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    },
    SEND_KEYS {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.findElement(By.xpath(locator)).sendKeys(value);
            return true;
        }
    },
    SEND_KEYS_TAB {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            element.sendKeys(value);
            element.sendKeys(Keys.TAB);
            return true;
        }
    },
    SEND_KEYS_ENTER {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            element.sendKeys(value);
            element.sendKeys(Keys.ENTER);
            return true;
        }
    },
    CLICK {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.findElement(By.xpath(locator)).click();
            return true;
        }
    },
    CLEAR {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.findElement(By.xpath(locator)).clear();
            return true;
        }
    },
    IS_ELEMENT_DISPLAYED {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            try {
                return driver.findElement(By.xpath(locator)).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        }
    },
    IS_ELEMENT_ENABLED {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            try {
                return driver.findElement(By.xpath(locator)).isEnabled();
            } catch (Exception e) {
                return false;
            }
        }
    },
    IS_ELEMENT_SELECTED {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            try {
                return driver.findElement(By.xpath(locator)).isSelected();
            } catch (Exception e) {
                return false;
            }
        }
    },
    GET_TEXT {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            String text = driver.findElement(By.xpath(locator)).getText();
            setGlobalVariable(text, "element.text");
            return true;
        }
    },
    GET_TAG_NAME {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            String tagName = driver.findElement(By.xpath(locator)).getTagName();
            setGlobalVariable(tagName, "element.tag.name");
            return true;
        }
    },
    GET_ATTRIBUTE {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            String attribute = driver.findElement(By.xpath(locator)).getAttribute(value);
            setGlobalVariable(attribute, "element.attribute");
            return true;
        }
    },
    GET_PARENT_WINDOW_HANDLE {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            String parentWindow = driver.getWindowHandle();
            setGlobalVariable(parentWindow, "parent.window.handle");
            return true;
        }
    },
    GET_CHILD_WINDOW_HANDLE {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String mainWindowHandle, String value) {
            Set<String> s1 = driver.getWindowHandles();
            Iterator<String> i1 = s1.iterator();
            while (i1.hasNext()) {
                String childWindow = i1.next();
                if (!mainWindowHandle.equalsIgnoreCase(childWindow)) {
                    setGlobalVariable(childWindow, "child.window.handle");
                }
            }
            return true;
        }
    },
    SWITCH_TO_WINDOW_HANDLE {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String windowHandle, String value) {
            driver.switchTo().window(windowHandle);
            return true;
        }
    },
    SWITCH_TO_FRAME {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String frame, String value) {
            driver.switchTo().frame(frame);
            return true;
        }
    },
    SWITCH_TO_PARENT_FRAME {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.switchTo().defaultContent();
            return true;
        }
    },
    SELECT_BY_VISIBLE_TEXT {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            Select dropdown = new Select(element);
            dropdown.selectByVisibleText(value);
            return true;
        }
    },
    SELECT_BY_VALUE {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            Select dropdown = new Select(element);
            dropdown.selectByValue(value);
            return true;
        }
    },
    SELECT_BY_INDEX {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            Select dropdown = new Select(element);
            dropdown.selectByIndex(Integer.parseInt(value));
            return true;
        }
    },
    GET_SELECTED_TEXT {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            Select dropdown = new Select(element);
            setGlobalVariable(dropdown.getFirstSelectedOption().getText(), "selected.dropdown.value");
            return true;
        }
    },
    ACCEPT_ALERT {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.switchTo().alert().accept();
            return false;
        }
    },
    DISMISS_ALERT {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.switchTo().alert().dismiss();
            return true;
        }
    },
    GET_ALERT_TEXT {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            String alertText = driver.switchTo().alert().getText();
            setGlobalVariable(alertText, "alert.text");
            return true;
        }
    },
    ALERT_SEND_KEYS {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            driver.switchTo().alert().sendKeys(value);
            return true;
        }
    },
    IS_ALERT_PRESENT {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String timeOut) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Integer.parseInt(timeOut));
                wait.until(ExpectedConditions.alertIsPresent());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    },
    MOUSE_HOVER {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String timeOut) {
            Actions action = new Actions(driver);
            action.moveToElement(driver.findElement(By.xpath(locator))).perform();
            return true;
        }
    },
    DRAG_DROP {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String fromLocator, String toLocator) {
            WebElement from = driver.findElement(By.xpath(fromLocator));
            WebElement to = driver.findElement(By.xpath(toLocator));
            Actions act = new Actions(driver);
            act.dragAndDrop(from, to).build().perform();
            return true;
        }
    },
    RIGHT_CLICK {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            Actions action = new Actions(driver);
            action.contextClick(element).build().perform();
            return true;
        }
    },
    DOUBLE_CLICK {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            Actions action = new Actions(driver);
            action.doubleClick(element).build().perform();
            return true;
        }
    },
    SELECT_CHECKBOX {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            if (!element.isSelected()) {
                element.click();
            }
            return true;
        }
    },
    UN_SELECT_CHECKBOX {
        @Override
        public boolean executeCommand(WebDriver driver, Robot robot, String locator, String value) {
            WebElement element = driver.findElement(By.xpath(locator));
            if (element.isSelected()) {
                element.click();
            }
            return true;
        }
    },
        MOUSE_HOVER_AND_CLICK {
            @Override
            public boolean executeCommand(WebDriver driver, Robot robot, String locator, String timeOut) {
                Actions action = new Actions(driver);
                action.moveToElement(driver.findElement(By.xpath(locator))).click().build().perform();
                return true;
            }
    };

    public abstract boolean executeCommand(WebDriver driver, Robot robot, String locator, String value);

    public void setGlobalVariable(String value, String variable) {
        Configvariable configvariable = new Configvariable();
        configvariable.setStringVariable(value, variable);
    }


}
