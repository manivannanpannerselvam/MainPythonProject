package com.prod.tab.filehandling;

import com.prod.tap.filehandling.ZipFolder;
import org.testng.annotations.Test;

public class ZipFolderTest {


    @Test
    public void testGetJsonString() {
        ZipFolder.zipFilesAndFolder("reports/cucumber","reports/cucumber.zip");
    }


}
