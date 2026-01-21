package com.prod.tap.api;

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

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    @Autowired
    private Configvariable configvariable;

    private MultipartEntityBuilder multipartEntityBuilder = null;
    private HttpResponse httpresponse = null;
    private String responseBody = null;

    private static String PROXY_USER = System.getProperty("proxy.user");
    private static String PROXY_PASS = System.getProperty("proxy.pass");
    private static String PROXY_HOST = "10.163.39.77";
    private static int PROXY_PORT = 8080;

    public CloseableHttpClient createHttpClient(String targetHostUser, String targetHostPassword) {
        CredentialsProvider result = new BasicCredentialsProvider();
        result.setCredentials(new AuthScope(PROXY_HOST, PROXY_PORT), new UsernamePasswordCredentials(targetHostUser, targetHostPassword));
//        result.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(targetHostUser, targetHostPassword));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(result).build();
        return httpClient;
    }

    public CloseableHttpClient createHttpClient1() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return httpClient;
    }

    public void closeHttpClent(CloseableHttpClient httpClient) {
        httpClient.getConnectionManager().shutdown();
        multipartEntityBuilder = null;
//        httpresponse = null;
    }

    public RequestConfig setProxyConfig() {
        HttpHost proxyHost = new HttpHost(PROXY_HOST, PROXY_PORT, "http");

        //Setting the proxy
        RequestConfig.Builder reqconfigconbuilder = RequestConfig.custom();
        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxyHost);
        return reqconfigconbuilder.build();
    }

    public HttpPost createHttpPost(String endpointUrl, Map<String, String> sendHeaders) {
        RequestConfig config=null;
        if (configvariable.isProxyRequired()) {
            config = setProxyConfig();
        }
        HttpPost httpPost = new HttpPost(endpointUrl);
        if (sendHeaders != null) {
            for (Map.Entry<String, String> e : sendHeaders.entrySet()) {
                httpPost.addHeader(e.getKey(), e.getValue());
            }
        }
        httpPost.setConfig(config);
        return httpPost;
    }

    public HttpGet createHttpGet(String endpointUrl, Map<String, String> sendHeaders) {
        RequestConfig config=null;
        if (configvariable.isProxyRequired()) {
            config = setProxyConfig();
        }
        HttpGet httpGet = new HttpGet(endpointUrl);
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        if (sendHeaders != null) {
            for (Map.Entry<String, String> e : sendHeaders.entrySet()) {
                httpGet.setHeader(e.getKey(), e.getValue());
            }
        }
        //Setting the config to the request
        httpGet.setConfig(config);
        return httpGet;
    }

    public HttpPost setBodyParameters(HttpPost httpPost, String apiBodyParameters) {
        //Set the request post body
        StringEntity userEntity = null;
        try {
            userEntity = new StringEntity(apiBodyParameters);
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to set body parameter");
        }
        httpPost.setEntity(userEntity);
        return httpPost;
    }

    public HttpResponse getHTTPPostResponse(CloseableHttpClient httpClient, HttpPost httpPost) {
        //Send the request; It will immediately return the response in HttpResponse object if any
        try {
            httpresponse = httpClient.execute(httpPost);
            responseBody = EntityUtils.toString(httpresponse.getEntity());
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to get response from post request");
        }
        return httpresponse;
    }

    public HttpResponse getHTTPGetResponse(CloseableHttpClient httpClient, HttpGet httpGet) {
        //Send the request; It will immediately return the response in HttpResponse object if any
        try {
            httpresponse = httpClient.execute(httpGet);
            responseBody = EntityUtils.toString(httpresponse.getEntity());
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to get response from get request");
        }
        return httpresponse;
    }

    public int getResponseCode() {
        //verify the valid error code first
        int statusCode = 0;
        String responseBody = null;
        try {
            statusCode = httpresponse.getStatusLine().getStatusCode();
        } catch (Exception e) {
            LOGGER.error("error: ", e);
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "API call failed with status [{}]", statusCode);
        }
        return statusCode;
    }

    public void addMultiPartAsFile(String keyName, File file) {
        if (multipartEntityBuilder == null) {
            multipartEntityBuilder = MultipartEntityBuilder.create();
            //Setting the mode
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        }
        //Adding a file
        multipartEntityBuilder.addBinaryBody("Users.csv", file);
    }

    public void addMultiPartAsText(String keyName, String text) {
        if (multipartEntityBuilder == null) {
            multipartEntityBuilder = MultipartEntityBuilder.create();
            //Setting the mode
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        }
        //Adding text
        multipartEntityBuilder.addTextBody(keyName, text);
    }

    public HttpEntity getHttpEntity() {
        //Building a single entity using the parts
        return multipartEntityBuilder.build();
    }

    public HttpUriRequest createMultiPartPostRequest(String url) {
        //Building the RequestBuilder request object
        RequestBuilder reqbuilder = RequestBuilder.post(url);

        //Set the entity object to the RequestBuilder
        reqbuilder.setEntity(getHttpEntity());

        //Building the request
        return reqbuilder.build();
    }

    public String executeRequestAndGetResponse(CloseableHttpClient httpClient, String url) {
        try {
            HttpUriRequest multipartRequest = createMultiPartPostRequest(url);
            //Executing the request
            httpresponse = httpClient.execute(multipartRequest);
            responseBody = EntityUtils.toString(httpresponse.getEntity());
            LOGGER.info("Receiving:" + responseBody);
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Failed to send request and get response {}", e.getMessage());
        }
        return responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

}
