package com.prod.tap.api;

import com.prod.tap.config.Configvariable;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.prod.tap.constants.Constants.PROXY_PORT;
import static com.prod.tap.constants.Constants.PROXY_URL;
import static io.restassured.RestAssured.given;

@Component
public class RestAPIMethods {
    private static final Logger LOGGER = Logger.getLogger(RestAPIMethods.class);

    @Autowired
    private Configvariable configvariable;

    private Response response;

    private RequestSpecification requestSpec;

    private String endPoint = null;

    private String baseURL = null;

    private Map<String, String> endPointParams = new HashMap<>();

    private static String OS = System.getProperty("os.name").toLowerCase();

//    static {
//        restProxySetter();
//    }

    public synchronized Response sendRequest(String requestType) {
        String url = null;
        try {
            url = baseURL + endPoint;
            LOGGER.info("Sending " + requestType + " request to " + url);

            switch (requestType) {
                case "GET":
                    response = this.sendGetRequest(url);
                    break;
                case "POST":
                    response = this.sendPostRequest(url);
                    break;
                case "DELETE":
                    response = this.sendDeleteRequest(url);
                    break;
                case "PUT":
                    response = this.sendPutRequest(url);
                    break;
                case "PATCH":
                    response = this.sendPatchRequest(url);
                    break;
                default:
            }
//            response.then().log().ifError();
            return response;
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED.PROCESSING_FAILED, "Not able to place api request for [{}]", url);
        } finally {
            this.clearRequestSpecObject();
        }
    }

    public void clearRequestSpecObject() {
        requestSpec = null;
    }


    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }


    public Response getResponse() {
        return response;
    }


    public void setApiBaseUri(final String uri) {
        this.baseURL = uri;
    }


    public void setApiEndPoint(final String endPoint) {
        this.endPoint = endPoint;
    }


    public void setEndPointParamsVar(final Map<String, String> params) {
        this.endPointParams = params;
    }


    //Setting Basic Authentication
    public RequestSpecification setBasicAuthentication(final String username, final String password) {
        LOGGER.warn("Basic authentication using " + username + " and " + password);
        if (getRequestSpec() == null) {
            requestSpec = given().auth().basic(username, password);
        } else {
            requestSpec = getRequestSpec().given().auth().basic(username, password);
        }
        return requestSpec;
    }


    public RequestSpecification setBasicAuthentication(final String token) {
        LOGGER.warn("Basic authentication using token " + token);
        if (getRequestSpec() == null) {
            requestSpec = given().auth().oauth2(token);
        } else {
            requestSpec = getRequestSpec().given().auth().oauth2(token);
        }
        return requestSpec;
    }


    //Setting Header params
    public RequestSpecification setHeaderParams(final Map<String, String> headerParams) {
        LOGGER.warn("Setting Api header params " + headerParams.toString());
        if (getRequestSpec() == null) {
            requestSpec = given().headers(headerParams);
        } else {
            requestSpec = getRequestSpec().given().headers(headerParams);
        }
        return requestSpec;
    }


    //Setting Body Params
    public RequestSpecification setBodyParam(final String bodyContent) {
        LOGGER.warn("Setting Api body param " + bodyContent);
        if (getRequestSpec() == null) {
            requestSpec = given().body(bodyContent);
        } else {
            requestSpec = getRequestSpec().given().body(bodyContent);
        }
        return requestSpec;
    }

    public RequestSpecification setBodyParam(final Map<String, String> bodyContent) {
        LOGGER.warn("Setting Api body param " + bodyContent);
        if (getRequestSpec() == null) {
            requestSpec = given().formParams(bodyContent);
        } else {
            requestSpec = getRequestSpec().given().formParams(bodyContent);
        }
        return requestSpec;
    }


    public RequestSpecification setBodyParam(String key, String value) {
        LOGGER.warn("Setting Api body param " + value);
        if (getRequestSpec() == null) {
            requestSpec = given().formParams(key, value);
        } else {
            requestSpec = getRequestSpec().given().formParams(key, value);
        }
        return requestSpec;

    }

    //API interaction methods

    public Response sendPostRequest(final String url) {
        try {
            if (getRequestSpec() == null) {
                return given()
                        .when()
                        .post(url);
            }

            return getRequestSpec()
                    .when()
                    .post(url);
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to place Post request for [{}]", url);
        }
    }

    public Response sendPostRequest(final String url, Map<String, String> data) {
        try {
            if (getRequestSpec() == null) {
                return given()
                        .when()
                        .post(url);
            }

            return getRequestSpec()
                    .when()
                    .post(url);
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to place Post request for [{}]", url);
        }
    }


    public Response sendGetRequest(final String url) {
        try {
            if (getRequestSpec() == null) {
                return given()
                        .when()
                        .params(endPointParams)
                        .get(url);
            }

            return getRequestSpec()
                    .given()
                    .params(endPointParams)
                    .when()
                    .get(url);
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to place Get request for [{}]", url);
        }
    }


    public Response sendDeleteRequest(final String url) {
        try {
            if (getRequestSpec() == null) {
                return given()
                        .when()
                        .delete(url);
            }

            return getRequestSpec()
                    .when()
                    .delete(url);
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to place delete request for [{}]", url);
        }
    }


    public Response sendPutRequest(final String url) {
        try {
            if (getRequestSpec() == null) {
                return given()
                        .when()
                        .put(url);
            }

            return getRequestSpec()
                    .when()
                    .put(url);
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to place put request for [{}]", url);
        }
    }


    public Response sendPatchRequest(final String url) {
        if (getRequestSpec() == null) {
            return given()
                    .when()
                    .patch(url);
        }

        return getRequestSpec()
                .when()
                .patch(url);
    }


    //Validation methods

    public Integer getStatusCode() {
        return getResponse().getStatusCode();
    }


    public String getResponseAsString() {
        return getResponse().asString();
    }


    public String getValueFromResponse(final String jsonKey) {
        return ((ArrayList) getObjectFromResponse(jsonKey)).get(0).toString();
    }


    public Object getObjectFromResponse(final String jsonKey) {
        return this.getObjectFromResponse(getResponse(), jsonKey);
    }


    public Object getObjectFromResponse(final Response response, final String jsonKey) {
        if (response == null) {
            return null;
        }
        Object value = response.jsonPath().get(jsonKey);
        LOGGER.warn("Response value for jsonKey " + jsonKey + " is " + value);
        return value;
    }


    public <T> T getResponseIntoObject(final Response response, final String jsonKey, final Class<T> clazz) {
        if (response == null) {
            return null;
        }
        return response.jsonPath().getObject(jsonKey, clazz);
    }

    //  set string values for "proxy" and "port" from config
    public  void restProxySetter() {
        if (PROXY_URL != null && (configvariable.isProxyRequired())) {
            RestAssured.proxy(ProxySpecification.host(PROXY_URL));
            RestAssured.proxy(ProxySpecification.port(Integer.parseInt(PROXY_PORT)));
        }
    }

    public RequestSpecification setMultiPartFormData(String key, String filePath, String fileType) {
        LOGGER.warn("Setting Api body Multipart form param " + filePath);
        if (getRequestSpec() == null) {
            requestSpec = given().multiPart(key, new File(filePath), fileType);
        } else {
            requestSpec = getRequestSpec().given().multiPart(key, new File(filePath), fileType);
        }
        return requestSpec;

    }

    public RequestSpecification setMultiPartFormData(String key, String filePath) {
        LOGGER.warn("Setting Api body Multipart form param " + filePath);
        if (getRequestSpec() == null) {
            requestSpec = given().multiPart(key, new File(filePath));
        } else {
            requestSpec = getRequestSpec().given().multiPart(key, new File(filePath));
        }
        return requestSpec;

    }

}
