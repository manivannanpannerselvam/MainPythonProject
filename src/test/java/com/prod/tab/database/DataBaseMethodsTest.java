package com.prod.tab.database;

import com.prod.tap.database.DatabaseMethods;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.Map;

public class DataBaseMethodsTest {
    private DatabaseMethods databaseMethods = new DatabaseMethods();

    @Test
    public void testConnectToSqlite() {
        String filePath = "target/test-classes/testFile/RKStorage";
        Connection con = databaseMethods.connectToSqlite(filePath);
        Assert.assertNotNull(con);
    }

    @Test
    public void testSelectFromSqliteDB() {
        String filePath = "target/test-classes/testFile/RKStorage";
        Map<String, String> dataMap = databaseMethods.selectFromSqliteDB(filePath, "SELECT value as FORGOT_PASSWORD FROM catalystLocalStorage WHERE key in (\"persist:forgotPassword\")");
        Assert.assertNotNull(dataMap);
    }


}
