package com.prod.tab.filehandling;

import com.prod.tap.filehandling.JsonReader;
import com.prod.tap.filehandling.JsonReader;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class JsonReaderTest {

    private JsonReader jsonReader = new JsonReader();

    @Test
    public void testGetJsonString() {
        String loginText = jsonReader.getJsonString("/testFile/Sales.json");
        assertFalse(loginText.isEmpty());
    }


    @Test
    public void testConvertJsonStringToMap() {
        String jsonText = "{\n" +
                "  \"operation\": \"login\",\n" +
                "  \"body\": {\n" +
                "    \"loginId\": \"TestUser\",\n" +
                "    \"password\": \"TestPassword\"\n" +
                "  }\n" +
                "}";

        Map<String, Object> loginText = jsonReader.convertJsonStringToMap(jsonText);
        Map<String, String> loginIDMap = (Map<String, String>) loginText.get("body");
        assertEquals(loginIDMap.get("loginId"), "TestUser");
    }

    @Test
    public void testConvertJsonStringToJsonObject() {
        String jsonText = "{\n" +
                "  \"operation\": \"login\",\n" +
                "  \"body\": {\n" +
                "    \"loginId\": \"TestUser\",\n" +
                "    \"password\": \"TestPassword\"\n" +
                "  }\n" +
                "}";

        JSONObject loginText = jsonReader.convertJsonStringToJsonObject(jsonText);
        Map<String, Object> loginIDMap = loginText.getJSONObject("body").toMap();
        assertEquals(loginIDMap.get("loginId"), "TestUser");
    }

}
