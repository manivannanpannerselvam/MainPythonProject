package com.prod.tap.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class HttpClientApi {


    private static final Logger logger = LoggerFactory.getLogger(HttpClientApi.class);
    @Autowired
    private Configvariable configvariable;
    private MultipartEntityBuilder multipartEntityBuilder = null;
    private HttpResponse httpresponse = null;
    private String responseBody = null;
    private CloseableHttpClient httpClient = null;
    private Map<String, String> sendHeaders = new HashMap();
    private HttpPost httpPost = null;
    private HttpGet httpGet = null;
    private String endpointUrl;
    private String requestBody = null;
    private static String PROXY_USER = System.getProperty("proxy.user");
    private static String PROXY_PASS = System.getProperty("proxy.pass");
    private static String PROXY_HOST = System.getProperty("proxy.host");
    private static int PROXY_PORT = 8080;

    public HttpClientApi() {
    }

    public String getUrl() {
        return this.endpointUrl;
    }

    public void setUrl(String endpointUrl) {
        logger.info("Endpoint url is " + endpointUrl);
        this.endpointUrl = endpointUrl;
    }

    public String getRequestBody() {
        return this.requestBody;
    }

    public void setRequestBody(String requestBody) {
        logger.info("Request body is " + requestBody);
        this.requestBody = requestBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getSendHeaders() {
        return this.sendHeaders;
    }

    public void setSendHeaders(Map<String, String> sendHeaders) {
        logger.info("Header map is  " + sendHeaders);
        this.sendHeaders = sendHeaders;
    }

    public void setSendHeaders(String key, String value) {
        logger.info("Set header key as  " + key + " and value as " + value);
        if (this.sendHeaders == null) {
            this.sendHeaders = new HashMap();
        }

        if (this.sendHeaders.containsKey(key)) {
            this.sendHeaders.remove(key);
        }

        this.sendHeaders.put(key, value);
    }

    public CloseableHttpClient createHttpClient(String targetHostUser, String targetHostPassword) {
        CredentialsProvider result = new BasicCredentialsProvider();
        result.setCredentials(new AuthScope(PROXY_HOST, PROXY_PORT), new UsernamePasswordCredentials(targetHostUser, targetHostPassword));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(result).build();
        return httpClient;
    }

    public CloseableHttpClient createHttpClient1() {
        if (this.httpClient != null) {
            this.closeHttpClent();
            this.resetVariables();
            this.sendHeaders = null;
        }

        this.httpClient = HttpClients.createDefault();
        return this.httpClient;
    }

    public void closeHttpClent() {
        this.httpClient.getConnectionManager().shutdown();
        this.httpresponse = null;
    }

    public RequestConfig setProxyConfig() {
        HttpHost proxyHost = new HttpHost(PROXY_HOST, PROXY_PORT, "http");
        RequestConfig.Builder reqconfigconbuilder = RequestConfig.custom();
        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxyHost);
        return reqconfigconbuilder.build();
    }

    public HttpPost createHttpPost() {
        RequestConfig config = null;
        if (this.configvariable.isProxyRequired()) {
            config = this.setProxyConfig();
        }

        this.httpPost = new HttpPost(this.getUrl());
        if (this.sendHeaders != null) {
            Iterator var2 = this.sendHeaders.entrySet().iterator();

            while(var2.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry)var2.next();
                this.httpPost.addHeader(this.configvariable.expandValue((String)e.getKey()), this.configvariable.expandValue((String)e.getValue()));
            }
        }

        this.httpPost.setConfig(config);
        return this.httpPost;
    }

    public HttpGet createHttpGet() {
        RequestConfig config = null;
        if (this.configvariable.isProxyRequired()) {
            config = this.setProxyConfig();
        }

        this.httpGet = new HttpGet(this.endpointUrl);
        this.httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        if (this.sendHeaders != null) {
            Iterator var2 = this.sendHeaders.entrySet().iterator();

            while(var2.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry)var2.next();
                this.httpGet.setHeader(this.configvariable.expandValue((String)e.getKey()), this.configvariable.expandValue((String)e.getValue()));
            }
        }

        this.httpGet.setConfig(config);
        return this.httpGet;
    }

    public void setBodyParameters() {
        StringEntity userEntity = null;
        if (this.requestBody != null) {
            try {
                userEntity = new StringEntity(this.requestBody);
            } catch (IOException var3) {
                throw new TapException(TapExceptionType.IO_ERROR, "Failed to set body parameter", new Object[0]);
            }

            this.httpPost.setEntity(userEntity);
        }

    }

    public HttpResponse getHTTPPostResponse() {
        try {
            this.httpresponse = this.httpClient.execute(this.httpPost);
            this.responseBody = EntityUtils.toString(this.httpresponse.getEntity());
            logger.info("Receiving:" + this.responseBody);
        } catch (IOException var2) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to get response from post request", new Object[0]);
        }

        return this.httpresponse;
    }

    public HttpResponse getHTTPGetResponse() {
        try {
            this.httpresponse = this.httpClient.execute(this.httpGet);
            this.responseBody = EntityUtils.toString(this.httpresponse.getEntity());
            logger.info("Receiving:" + this.responseBody);
        } catch (IOException var2) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to get response from get request", new Object[0]);
        }

        return this.httpresponse;
    }

    public int getResponseCode() {
        int statusCodes = 0;
        Object var2 = null;

        try {
            int statusCode = this.httpresponse.getStatusLine().getStatusCode();
            return statusCode;
        } catch (Exception var4) {
            logger.error("error: ", var4);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "API call failed with status [{}]", new Object[]{Integer.valueOf(statusCodes)});
        }
    }

    public void addMultiPartAsFile(String keyName, File file) {
        if (this.multipartEntityBuilder == null) {
            this.multipartEntityBuilder = MultipartEntityBuilder.create();
            this.multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        }

        this.multipartEntityBuilder.addBinaryBody(keyName, file);
    }

    public void addMultiPartAsText(String keyName, String text) {
        if (this.multipartEntityBuilder == null) {
            this.multipartEntityBuilder = MultipartEntityBuilder.create();
            this.multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        }

        this.multipartEntityBuilder.addTextBody(keyName, text);
    }

    public HttpEntity getHttpEntity() {
        if (this.requestBody != null) {
            HttpEntity httpEntity = EntityBuilder.create().setText(this.requestBody).build();
            return httpEntity;
        } else {
            return this.multipartEntityBuilder != null ? this.multipartEntityBuilder.build() : null;
        }
    }

    public HttpUriRequest createMultiPartPostRequest(String method, String url) {
        RequestBuilder reqbuilder = RequestBuilder.create(method.toUpperCase()).setUri(url);
        if (this.configvariable.isProxyRequired()) {
            reqbuilder.setConfig(this.setProxyConfig());
        }

        if (this.sendHeaders != null) {
            Iterator var4 = this.sendHeaders.entrySet().iterator();

            while(var4.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry)var4.next();
                reqbuilder.addHeader(this.configvariable.expandValue((String)e.getKey()), this.configvariable.expandValue((String)e.getValue()));
            }
        }

        if (this.getHttpEntity() != null) {
            reqbuilder.setEntity(this.getHttpEntity());
        }

        return reqbuilder.build();
    }

    public String executeRequestAndGetResponse(String method) {
        try {
            HttpUriRequest multipartRequest = this.createMultiPartPostRequest(method, this.getUrl());
            this.httpresponse = this.httpClient.execute(multipartRequest);
            this.responseBody = EntityUtils.toString(this.httpresponse.getEntity());
            logger.info("Receiving:" + this.responseBody);
        } catch (IOException var3) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to send request and get response {}", new Object[]{var3.getMessage()});
        }

        return this.responseBody;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public JsonNode getJsonNodeFromResponse() {
        JsonNode jsonNode = null;
        String jsonData = "";

        try {
            jsonData = this.responseBody;
            jsonNode = (new ObjectMapper()).readTree(jsonData);
            return jsonNode;
        } catch (Exception var4) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to convert response json data into json node [{}]", new Object[]{jsonData});
        }
    }

    public String getJsonPathStringValue(String path) {
        String val = "";

        try {
            val = JsonPath.read(this.responseBody, path, new Predicate[0]).toString();
            logger.info("Value for node " + path + " is " + val);
            return val;
        } catch (Exception var4) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Please provide correct Json Path to get data [{}]", new Object[]{path});
        }
    }

    public List<Object> getJsonPathListValue(String path) {
        new ArrayList();

        try {
            List<Object> val = (List)JsonPath.read(this.responseBody, path, new Predicate[0]);
            logger.info("Value for node " + path + " is " + val);
            return val;
        } catch (Exception var4) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Please provide correct Json Path to get data [{}]", new Object[]{path});
        }
    }

    public void resetVariables() {
        this.multipartEntityBuilder = null;
        this.requestBody = null;
    }
}
