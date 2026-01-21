package com.prod.tab.api;

import com.prod.tap.api.RestAPIMethods;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class RestAPIMethodsTest {
//    private static final Logger logger = LoggerFactory.getLogger(RestAPIMethodsTest.class);

    @Spy
    @InjectMocks
    private RestAPIMethods restMocks = new RestAPIMethods();

    @Mock
    private Response responseMock;

    @Mock
    private RequestSpecification requestSpecificationMock;

    @BeforeTest
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testsendRequestPost() {
        restMocks.setApiBaseUri("https://fake_url.com");
        restMocks.setApiEndPoint("/test");
        doReturn(responseMock).when(restMocks).sendPostRequest(anyObject());
        restMocks.sendRequest("POST");
        verify(restMocks, times(1)).sendPostRequest(anyObject());
    }

    @Test
    public void testsendRequestGet() {
        restMocks.setApiBaseUri("https://fake_url.com");
        restMocks.setApiEndPoint("/test");
        doReturn(responseMock).when(restMocks).sendGetRequest(anyObject());
        restMocks.sendRequest("GET");
        verify(restMocks, times(1)).sendGetRequest(anyObject());
    }

    @Test
    public void testsendRequestPUT() {
        restMocks.setApiBaseUri("https://fake_url.com");
        restMocks.setApiEndPoint("/test");
        doReturn(responseMock).when(restMocks).sendPutRequest(anyObject());
        restMocks.sendRequest("PUT");
        verify(restMocks, times(1)).sendPutRequest(anyObject());
    }

    @Test
    public void testsendRequestDelete() {
        restMocks.setApiBaseUri("https://fake_url.com");
        restMocks.setApiEndPoint("/test");
        doReturn(responseMock).when(restMocks).sendDeleteRequest(anyObject());
        restMocks.sendRequest("DELETE");
        verify(restMocks, times(1)).sendDeleteRequest(anyObject());
    }

    @Test
    public void testsendRequestPatch() {
        restMocks.setApiBaseUri("https://fake_url.com");
        restMocks.setApiEndPoint("/test");
        doReturn(responseMock).when(restMocks).sendPatchRequest(anyObject());
        restMocks.sendRequest("PATCH");
        verify(restMocks, times(1)).sendPatchRequest(anyObject());
    }

    @Test
    public void testgetRequestSpec() {
        assertEquals(restMocks.getRequestSpec(), requestSpecificationMock);
    }

    @Test
    public void testgetResponse() {
        assertEquals(restMocks.getResponse(), responseMock);
    }

}
