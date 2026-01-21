package com.prod.tap.config;

import com.prod.tap.constants.Constants;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Component
public class Configvariable {
    private static final Logger logger = Logger.getLogger(Configvariable.class);

    public static Map<String, String> globalPropertyMap = new HashMap<>();
    public static Map<String, String> envPropertyMap = new HashMap<>();

    public String expandValue(String expression) {
        String originalExp = expression;
        if (expression == null)
            return null;
        int varStart = expression.indexOf("${");
        if (varStart >= 0) {
            String varName = "";
            String expanded = "";
            while (varStart >= 0) {
                int varEnd = originalExp.indexOf('}', varStart + 2);
                if (varEnd > varStart + 1) {
                    varName = originalExp.substring(varStart + "${".length(), varEnd);
                    String value = this.getStringVar(varName);
                    expanded = originalExp.substring(0, varStart) + value + originalExp.substring(varEnd + 1);
                }
                originalExp = expanded;
                varStart = expanded.indexOf("${");
            }
            return expanded;
        }
        return originalExp;
    }

    public String getStringVar(String variable) {
        String result = "";
        if (globalPropertyMap.containsKey(variable)) {
            result = globalPropertyMap.get(variable);
        }
        return result;
    }

    public void setStringVariable(String value, String variable) {
        if (globalPropertyMap.containsKey(variable)) {
            globalPropertyMap.remove(variable);
            globalPropertyMap.put(variable, value);
        } else {
            globalPropertyMap.put(variable, value);
        }
    }

    public void assignValueToVar(String value, String variable) {
        String expandedVal = expandValue(value);
        setStringVariable(expandedVal, variable);
    }

    public String getFormattedString(String locator, String value) {
        return String.format(locator, value);
    }

    public String generateRandomNumber(String format) {
        DateTimeFormatter dateTimeFormatter;
        dateTimeFormatter = DateTimeFormatter.ofPattern(format, Locale.getDefault());
        logger.info("Random number generated");
        return dateTimeFormatter.format(LocalDateTime.now());
    }

    public String getBaseDirectory() {
        String absolutePath = Paths.get(".").toAbsolutePath().normalize().toString();
        return absolutePath;
    }

    public String getClasspath(String fileName) {
        File file = new File(fileName);
        if (!file.isAbsolute()) {
            URL fileUrl = getClass().getResource(fileName);
            return new File(fileUrl.getFile()).getPath();
        } else {
            return file.getPath();
        }

    }

    public void assignValueToVarMap(Map<String, String> variableMap) {
        for (String key : variableMap.keySet()) {
            String expandedVal = expandValue(variableMap.get(key));
            setStringVariable(expandedVal, key);
        }
    }

    public String minusYearFromCurrentDate(int minusYear, String format) {
        LocalDateTime today = LocalDateTime.now();     //Today
        LocalDateTime previousDate = today.minusYears(minusYear);
        DateTimeFormatter dateTimeFormatter;
        dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(previousDate);
    }

    public void setupEnvironmentProperties(String envName, String lbu) {
        InputStream initialStream = null;
        if (!lbu.isEmpty()) {
            if ("dev".equalsIgnoreCase(envName)) {
                initialStream = getClass().getResourceAsStream("/env/" + lbu + "/dev.properties");
            } else if ("uat".equalsIgnoreCase(envName)) {
                initialStream = getClass().getResourceAsStream("/env/" + lbu + "/uat.properties");
            } else if ("prod".equalsIgnoreCase(envName)) {
                initialStream = getClass().getResourceAsStream("/env/" + lbu + "/prod.properties");
            }
        } else {
            initialStream = getClass().getResourceAsStream("/env/dev.properties");
        }
        propertiesFileLoad(initialStream);
    }

    public String formatDateAndTime(String format, final LocalDateTime time) {
        DateTimeFormatter dateTimeFormatter;
        dateTimeFormatter = DateTimeFormatter.ofPattern(format, Locale.getDefault());
        return dateTimeFormatter.format(time);
    }

    public String formatDateAndTimeWithZone(String format, final ZoneId zone, final LocalDateTime time) {
        ZonedDateTime utcZonedTime = time.atZone(ZoneOffset.UTC);
        ZonedDateTime zonedTime = utcZonedTime.withZoneSameInstant(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.getDefault());
        return formatter.format(zonedTime);
    }


    public String formatDateWithDayNumberSuffix(final ZoneId zone, final LocalDateTime time) {
        ZonedDateTime utcZonedTime = time.atZone(ZoneOffset.UTC);
        ZonedDateTime zonedTime = utcZonedTime.withZoneSameInstant(zone);
        String dayNumberSuffix = getDayNumberSuffix(time.getDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'" + dayNumberSuffix + "'-MMMM-yyyy", Locale.getDefault());
        return formatter.format(zonedTime);
    }

    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public boolean checkStringPatterMatch(String patternText, String stringToMatch) {
        return stringToMatch.matches(patternText);
    }

    /**
     * this method loads properties files config and file having test data.
     *
     * @param initialStream android or ios, to load specific test data file.
     */
    public Properties propertiesFileLoad(InputStream initialStream) {
        Properties prop = new Properties();
        try {
            prop.load(initialStream);
            for (String key : prop.stringPropertyNames()) {
                envPropertyMap.put(key, prop.getProperty(key));
                setStringVariable(prop.getProperty(key), key);
            }
        } catch (IOException e) {
            logger.error("Failed to load properties file for environment:" + initialStream);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Failed to load properties file for environment: [{}]", initialStream);
        }
        return prop;
    }

    public boolean contains(String pattern, String text, Integer fromIndex) {
        if (fromIndex != null && fromIndex < text.length())
            return Pattern.compile(pattern).matcher(text).find();

        return Pattern.compile(pattern).matcher(text).find();
    }

    public void match(String expected, String actual) {
        if (expected == null) {
            throw new TapException(TapExceptionType.VALIDATION_FAILED, "string match expected, but expected is null");
        }
        if (actual == null) {
            throw new TapException(TapExceptionType.VALIDATION_FAILED, "string match expected, but actual value given is null");
        }
        if (!expected.equals(actual)) {
            throw new TapException(TapExceptionType.VALIDATION_FAILED, "string match expected, but expected [{}] does not match [{}]", expected, actual);
        }
    }

    public boolean isWindow() {
        return System.getProperty("os.name").toLowerCase().contains("window");
    }

    public boolean isProxyRequired() {
        if (System.getProperty(Constants.SYSTEM_PROXY_APPLY) != null) {
            return Boolean.parseBoolean(System.getProperty(Constants.SYSTEM_PROXY_APPLY));
        }else{
            if (getStringVar(Constants.SYSTEM_PROXY_APPLY).isEmpty()) {
                return Boolean.parseBoolean(Constants.DEFAULT_IS_PROXY);
            } else {
                return Boolean.parseBoolean(getStringVar(Constants.SYSTEM_PROXY_APPLY));
            }
        }
    }
}

