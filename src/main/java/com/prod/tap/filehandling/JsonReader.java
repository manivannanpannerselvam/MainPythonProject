package com.prod.tap.filehandling;

import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;


@Component
public class JsonReader {
    private static final Logger logger = Logger.getLogger(JsonReader.class);
    private JSONParser jsonParser;
    private JSONObject jsonObject;

    public String getJsonString(String jsonFileName) {
//        Reader reader = null;
        String jsonString = "";
        try {
            InputStream initialStream = getClass().getResourceAsStream(jsonFileName);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(initialStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            jsonObject = new JSONObject(responseStrBuilder.toString());
//            reader = new InputStreamReader(initialStream);
//            jsonParser = new JSONParser();
//            jsonObject = (JSONObject) jsonParser.parse(reader);
            jsonString = jsonObject.toString();

        } catch (Exception e) {
            logger.error("Not able to get Json string from json file [{}]" + jsonFileName);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to get Json string from json file [{}]", jsonFileName);
        }
        return jsonString;
    }

    public Map<String, Object> convertJsonStringToMap(String jsonValue) {
        Map<String, Object> jsonMAP = null;
        jsonParser = new JSONParser();
        try {
            jsonObject = new JSONObject(jsonValue);
//            jsonObject = (JSONObject) jsonParser.parse(jsonValue);
//            jsonMAP = (Map<String, Object>) jsonObject;
            jsonMAP = jsonObject.toMap();
        } catch (Exception e) {
            logger.error("Not able to convert Json string to map [{}]" + jsonValue);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to convert Json string to map [{}]", jsonValue);
        }

        return jsonMAP;
    }

    public JSONObject convertJsonStringToJsonObject(String jsonValue) {
        jsonParser = new JSONParser();
        try {
//            jsonObject = (JSONObject) jsonParser.parse(jsonValue);
            jsonObject = new JSONObject(jsonValue);
        } catch (Exception e) {
            logger.error("Not able to convert Json string to jsonObject [{}]" + jsonValue);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to convert Json string to jsonObject [{}]", jsonValue);
        }

        return jsonObject;
    }

    public JSONObject convertJsonArrayToJsonObject(JSONArray jsonValue, int index) {
        JSONObject object = null;

        try {
            object = jsonValue.getJSONObject(index);
        } catch (Exception e) {
            logger.error("Not able to convert Json array to jsonObject [{}]" + jsonValue);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to convert Json string to jsonObject [{}]", jsonValue);
        }
        return object;
    }
}