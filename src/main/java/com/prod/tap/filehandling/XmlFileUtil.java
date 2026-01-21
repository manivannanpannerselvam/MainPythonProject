package com.prod.tap.filehandling;

import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XmlFileUtil {
    DocumentBuilderFactory dbFactory = null;
    DocumentBuilder builder = null;
    Document doc;
    File file = null;
    private static final Logger logger = Logger.getLogger(XmlFileUtil.class);
    Map<String, String> dataMap = new HashMap();

    public Document getDocument(String fileName) {
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            builder = dbFactory.newDocumentBuilder();
            file = new File(fileName);
            doc = builder.parse(file);
            return doc;
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "IO Exception while processing file [{}]", fileName);
        } catch (ParserConfigurationException e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "unable to create the instance of a DocumentBuilder");
        } catch (SAXException e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "unable to parse the file", fileName);
        }
    }

    public Map<String, String> extractValueByTagName(String tagName, String filePath) {
        try {
            Document doc = getDocument(filePath);
            if (doc.getElementsByTagName(tagName) != null) {
                NodeList nodeList = doc.getElementsByTagName(tagName);
                getAllAttributeValueByTagName(nodeList);
            } else {
                logger.warn("file doesn't have value for: " + tagName);
            }
        } catch (Exception e) {
            logger.error("unable to extract tag name");
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "unable to extract tag name [{}]", tagName);
        }
        return dataMap;
    }

    public void getAllAttributeValueByTagName(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeAttributes = node.getAttributes();
                    for (int j = 0; j < nodeAttributes.getLength(); j++) {
                        Node attrNode = nodeAttributes.item(j);
                        if (attrNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                            logger.info("Attribute name is: " + attrNode.getNodeName());
                            logger.info("Attribute value is: " + attrNode.getNodeValue());
                            logger.info("node text is: " + node.getTextContent());
                            dataMap.put(attrNode.getNodeValue(), node.getTextContent());
                        }
                    }
                } else {
                    logger.info("Node name is: " + node.getNodeName());
                    logger.info("Node value is: " + node.getTextContent());
                    dataMap.put(node.getNodeName(), node.getTextContent());
                }
            }
        }
    }

    public Map<String, String> extractValueOfNodeByTagName(String tagName, String filePath) {
        try {
            Document doc = getDocument(filePath);
            if (doc.getElementsByTagName(tagName) != null) {
                NodeList nodeList = doc.getElementsByTagName(tagName);
                getAllNodeValue(nodeList);
            } else {
                logger.warn("file doesn't have value for: " + tagName);
            }
        } catch (Exception e) {
            logger.error("unable to extract tag name");
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "unable to extract tag name [{}]", tagName);
        }
        return dataMap;
    }

    public void getAllNodeValue(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            String nodeName = nodeList.item(i).getNodeName();
            String nodeValue = nodeList.item(i).getTextContent();
            logger.info("Node name is: " + nodeName);
            logger.info("Node value is: " + nodeValue);
            dataMap.put(nodeName, nodeValue);
        }
    }

    public Map<String, String> extractNodeTextByAttributeValue(String attributeValue, String filePath) {
        try {
            Document doc = getDocument(filePath);
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            if (nodeList.getLength() > 0) {
                getNodeTextByAttributeValue(nodeList, attributeValue);
            } else {
                logger.warn("file doesn't have value for: " + attributeValue);
            }
        } catch (Exception e) {
            logger.error("unable to extract attributeValue");
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "unable to extract tag name [{}]", attributeValue);
        }
        return dataMap;
    }

    public void getNodeTextByAttributeValue(NodeList nodeList, String attributeValue) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.hasAttributes()) {
                NamedNodeMap nodeAttributes = node.getAttributes();
                for (int j = 0; j < nodeAttributes.getLength(); j++) {
                    Node attrNode = nodeAttributes.item(j);
                    if (attrNode.getNodeType() == Node.ATTRIBUTE_NODE && attrNode.getNodeValue().equalsIgnoreCase(attributeValue)) {
                        logger.info("Attribute name is: " + attrNode.getNodeName());
                        logger.info("Attribute value is: " + attrNode.getNodeValue());
                        logger.info("node text is: " + node.getTextContent());
                        dataMap.put(attrNode.getNodeValue(), node.getTextContent());
                        break;
                    }
                }
            }
        }
    }
}

