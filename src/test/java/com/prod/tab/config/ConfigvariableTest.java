package com.prod.tab.config;

import com.prod.tap.config.Configvariable;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ConfigvariableTest {

    private Configvariable configvariable = new Configvariable();

    @Test
    public void testGetStringVar() {
        configvariable.setStringVariable("testValue", "TEST_VARIABLE");
        assertEquals(configvariable.getStringVar("TEST_VARIABLE"), "testValue");
    }

    @Test
    public void testExpandValue() {
        configvariable.setStringVariable("testValue", "TEST_VARIABLE");
        assertEquals(configvariable.expandValue("Expand_${TEST_VARIABLE}"), "Expand_testValue");
    }

    @Test
    public void testAssignValueToVar() {
        configvariable.assignValueToVar("testValue", "TEST_VARIABLE");
    }

    @Test
    public void testGetFormattedString() {
        assertEquals(configvariable.getFormattedString("Test formatted %s", "string"), "Test formatted string");
    }


    @Test
    public void testGenerateRandomNumber() {
        String randomNumber = configvariable.generateRandomNumber("YYYY-MM-dd");
        assertNotNull(randomNumber);
    }

    @Test
    public void testGetBaseDirectory() {
        String baseDirectory = configvariable.getBaseDirectory();
        assertNotNull(baseDirectory);
    }

    @Test
    public void testAssignValueToVarMap() {
        Map<String, String> varMap = new HashMap<>();
        varMap.put("TEST_VAR", "testvalue");
        configvariable.assignValueToVarMap(varMap);
        assertEquals(configvariable.getStringVar("TEST_VAR"), "testvalue");
    }

    @Test
    public void testMinusYearFromCurrentDate() {
        String randomNumber = configvariable.minusYearFromCurrentDate(1, "YYYY-MM-dd");
        assertNotNull(randomNumber);
    }

    @Test
    public void testSetupEnvironmentProperties() {
        configvariable.setupEnvironmentProperties("dev", "sg");
        assertEquals(configvariable.getStringVar("mydoc.doc.id5"), "41020");

        configvariable.setupEnvironmentProperties("uat", "sg");
        assertEquals(configvariable.getStringVar("mydoc.doc.id4"), "42127");

        configvariable.setupEnvironmentProperties("", "");
        assertEquals(configvariable.getStringVar("mydoc.doc.id5"), "41020");
    }

    @Test
    public void testFormatDateAndTime() {
        LocalDateTime from = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(15, 0, 0));
        String randomNumber = configvariable.formatDateAndTime("YYYY-MM-dd", from);
        assertNotNull(randomNumber);
    }

    @Test
    public void testClassPath() {
        String baseDirectory = configvariable.getClasspath(configvariable.getBaseDirectory() + "/src/test/resources/testFile/graphql/GTL.csv");
        assertNotNull(baseDirectory);
    }

}
