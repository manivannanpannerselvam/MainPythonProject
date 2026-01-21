package com.prod.tap.filehandling;

import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipFolder {
    private static final Logger logger = Logger.getLogger(ZipFolder.class);

    public static void zipFilesAndFolder(String srcFolder, String destZipFile) {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        try {
            fileWriter = new FileOutputStream(destZipFile);
            zip = new ZipOutputStream(fileWriter);
            addFolderToZip("", srcFolder, zip);
            zip.flush();
            zip.close();
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to zip folder {}", destZipFile);
        }

    }

    private static void addFileToZip(String path, String srcFile, ZipOutputStream zip) {
        File folder = new File(srcFile);
        try {
            if (folder.isDirectory()) {
                addFolderToZip(path, srcFile, zip);
            } else {
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to add file to zip folder");
        }

    }

    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) {
        File folder = new File(srcFolder);

        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }
}
