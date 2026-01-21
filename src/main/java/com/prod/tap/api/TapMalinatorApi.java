package com.prod.tap.api;

import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import com.prod.tap.filehandling.JsonReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TapMalinatorApi {
    private static final Logger logger = Logger.getLogger(TapMalinatorApi.class);

    @Autowired
    private Configvariable configvariable;

    @Autowired
    private WebSocketClient webSocketClient;

    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private HttpUtils httpUtils;

    private static final String SUPPORTED_DOMAIN = "mailinator.com";

    public static String MALINATOR_EMAIL = "wss://www.mailinator.com/ws/fetchinbox?zone=public&query=%s";
    public static String EMAIL_URL_FORMAT = "https://www.mailinator.com/fetch_email?zone=public&msgid=%s";
//    public static String MALINATOR_EMAIL = "https://www.mailinator.com/v3/index.jsp?zone=public&query=%s";

    public String getMessageIdFromMailinatorEmail(String emailAddress) {
        String[] parts = emailAddress.split("@");
        if (parts.length != 2) {
            logger.error("Wrong format of email");
        }
        if (!parts[1].equalsIgnoreCase(SUPPORTED_DOMAIN)) {
            logger.error("Unsupported email domain for Email OTP reader");
        }
        String url = configvariable.getFormattedString(MALINATOR_EMAIL, parts[0]);
        webSocketClient.disconnectToWebSocket();
        String response = webSocketClient.sendMalinatorRequestAndGetForResponse(url);
        logger.info("Receiving: " + response);
        Map<String, Object> responseMap = jsonReader.convertJsonStringToMap(response);
        String msgId=responseMap.get("id").toString();
        if (responseMap.get("id") == null) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to fetch email from mailinator [{}]", response);
        }
        logger.info("Mailinator ID is : " + msgId);
        configvariable.setStringVariable(msgId, "MSG_ID");
        return msgId;
    }


    public String readEmailFormat(String mailId) {
        CloseableHttpClient httpclient = this.httpUtils.createHttpClient1();
        String url = configvariable.getFormattedString(EMAIL_URL_FORMAT, mailId);
        HttpPost httppost = httpUtils.createHttpPost(url, null);
        HttpResponse response = httpUtils.getHTTPPostResponse(httpclient, httppost);
        logger.info(response.toString());
        if (response.getStatusLine().getStatusCode() != 200) {
            logger.error("Unable to read email from mailinator = " + mailId);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Unable to read email from mailinator [{}]", mailId);
        }
        String responseBody = httpUtils.getResponseBody();
        httpUtils.closeHttpClent(httpclient);

        return responseBody;
    }

}
