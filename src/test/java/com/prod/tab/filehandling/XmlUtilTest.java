package com.prod.tab.filehandling;

import com.prod.tap.filehandling.XmlFileUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.util.Map;

public class XmlUtilTest {

    private XmlFileUtil xmlFileUtil = new XmlFileUtil();

    @Test
    public void testGetDocument() {
        String fileName = "target/test-classes/testFile/pulseSharedPrefs.xml";
        Document doc = xmlFileUtil.getDocument(fileName);
        Assert.assertNotNull(doc, "Document instance is not created");
    }

    @Test
    public void testExtractValueByTagName() {
        String tagName = "string";
        String filePath = "target/test-classes/testFile/pulseSharedPrefs.xml";
        Map<String, String> dataMap = xmlFileUtil.extractValueByTagName(tagName, filePath);
        Assert.assertNotNull(dataMap.size(), "file is empty");
    }

    @Test
    public void testExtractValueOfNodeByTagName() {
        String tagName = "version";
        String filePath = "target/test-classes/testFile/pulseSharedPrefs.xml";
        Map<String, String> dataMap = xmlFileUtil.extractValueOfNodeByTagName(tagName, filePath);
        Assert.assertNotNull(dataMap.size(), "file is empty");
    }

    @Test
    public void testExtractAttributeTextByAttributeValue(){
        String attributeValue = "persist:sharedData";
        String filePath = "target/test-classes/testFile/pulseSharedPrefs.xml";
        Map<String, String> dataMap = xmlFileUtil.extractNodeTextByAttributeValue(attributeValue, filePath);
        Assert.assertNotNull(dataMap.size(), "file is empty");
    }
}
