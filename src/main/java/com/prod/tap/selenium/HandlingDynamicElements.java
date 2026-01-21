package com.prod.tap.selenium;

import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HandlingDynamicElements {
    private static final Logger logger = Logger.getLogger(HandlingDynamicElements.class);

    public int getBrokenElementCount(WebDriver driver, String tagName) {
        int invalidImageCount = 0;

        try {
            List<WebElement> imagesList = driver.findElements(By.tagName(tagName));
            logger.info("Total no. of broken elements are " + imagesList.size());
            for (WebElement imgElement : imagesList) {
                if (imgElement != null) {
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpGet request = new HttpGet(imgElement.getAttribute("src"));
                    HttpResponse response = client.execute(request);
                    if (response.getStatusLine().getStatusCode() != 200)
                        invalidImageCount++;
                }
            }

        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, e.getMessage());
        }
        return invalidImageCount;
    }

    public void slideHorizontallyUsingActionClass(WebDriver driver, String locator, int xOffset, int yOffset) {
        WebElement element = driver.findElement(By.xpath(locator));
        Actions builder = new Actions(driver);
//         We need to give below value in +00 to move slider to 50 and +130 to move slider to 100,-100 to move slider to 12
//         xOffset = -100;
//        yOffset=0 for horizontal
        builder.moveToElement(element).clickAndHold().moveByOffset(xOffset, yOffset)
                .release().perform();
    }
}
