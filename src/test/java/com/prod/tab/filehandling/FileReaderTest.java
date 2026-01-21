package com.prod.tab.filehandling;

import com.prod.tap.filehandling.FileReaderUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;

public class FileReaderTest {

    @Test
    public void testCreateDirectory() {
        String dirPath = "target/test-classes/reports/summary/testdir";
        FileReaderUtil.createDirectory(dirPath);
    }

    @Test
    public void testCopyFilesToDirectory() {
        String srcDirPath = "target/test-classes/testFile";
        String desDirPath = "target/test-classes/reports/testdircopy";
        FileReaderUtil.createDirectory(desDirPath);
        FileReaderUtil.copyFilesToDirectory(srcDirPath, desDirPath);
    }

    @Test
    public void testCopyDirectory() {
        String srcDirPath = "target/test-classes/testFile";
        String desDirPath = "target/test-classes/reports/testdircopy";
        FileReaderUtil.createDirectory(desDirPath);
        FileReaderUtil.copyDirectory(srcDirPath, desDirPath);
    }

    @Test
    public void testGetAllFilesFromDirectory() {
        String srcDirPath = "target/test-classes/env/sg";
        File srcDir = new File(srcDirPath);
        ArrayList fileList = FileReaderUtil.getAllFileNamesFromDirectory(srcDir);
        Assert.assertNotNull(fileList, "Files are present");
    }

    @Test
    public void testCreateFile() {
        String srcDirPath = System.getProperty("user.dir") + "/reports/androidLog.txt";
        FileReaderUtil.createFile(srcDirPath);
    }

    @Test
    public void testWriteToFile() {
        String srcDirPath = "reports/androidLog.txt";
        FileReaderUtil.createFile(srcDirPath);
        for (int i = 0; i < 3; i++) {
            FileReaderUtil.writeToFileUsingBufferWriter(srcDirPath, "Sample text" + i);
        }

    }

}
