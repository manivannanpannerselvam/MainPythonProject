package com.prod.tab.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prod.tap.api.GraphQlApi;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class GraphQlApiTest {

    @Test
    public void testparseGraphqlWithOutVariable() {
        String filepath = "target/test-classes/testFile/graphql/decodeToken.graphql";
        File file = new File(filepath);
        String graphqlPayload = GraphQlApi.parseGraphql(file, null, "query");
        Assert.assertNotNull(graphqlPayload);
    }

    @Test
    public void testparseGraphqlWithVariable() {
        String filepath = "target/test-classes/testFile/graphql/decodeToken_withvar.graphql";
        File file = new File(filepath);
        // Create a variables to pass to the graphql query
        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("jwttoken", "Test");
        String graphqlPayload = GraphQlApi.parseGraphql(file, variables, "query");
        Assert.assertNotNull(graphqlPayload);
    }


}
