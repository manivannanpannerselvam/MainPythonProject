package com.prod.tap.filehandling;

import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class FileReaderUtil {
    private final static Logger logger = Logger.getLogger(FileReaderUtil.class);

    public static void createDirectory(String dirPath) {
        try {
            if (dirPath != null && !"".equals(dirPath.trim())) {
                File dirFileObj = FileUtils.getFile(dirPath);

                if (!dirFileObj.exists()) {
                    FileUtils.forceMkdir(dirFileObj);
                    logger.info("Create " + dirPath + " success. ");
                } else {
                    logger.info(dirPath + " exist. ");
                }
            }
        } catch (IOException ex) {
            logger.error("Not able to create directory = " + dirPath);
            throw new TapException(TapExceptionType.IO_ERROR, "Not able to create directory [{}]", dirPath);
        }
    }

    public static void copyFilesToDirectory(String sourceLocation, String destLocation) {
        File srcDir = new File(sourceLocation);
        File desDir = new File(destLocation);
        for (File srcFile : srcDir.listFiles()) {
            try {
                if (srcFile.isDirectory()) {
                    FileUtils.copyDirectoryToDirectory(srcFile, desDir);
                } else {
                    FileUtils.copyFileToDirectory(srcFile, desDir);
                }
            } catch (IOException e) {
                throw new TapException(TapExceptionType.IO_ERROR, "Not able to copy file from directory [{}] to directory [{}]", sourceLocation, destLocation);
            }

        }
    }

    public static void copyDirectory(String sourceLocation, String destLocation) {
        File srcDir = new File(sourceLocation);
        File desDir = new File(destLocation);
        try {
            FileUtils.copyDirectoryToDirectory(srcDir, desDir);
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Not able to copy directory from location [{}] to directory [{}]", sourceLocation, destLocation);
        }
    }

    public static boolean isFileExist(String fileName) {
        File filePath = new File(fileName);
        return (filePath.exists() && !filePath.isDirectory());
    }

    public static ArrayList getAllFileNamesFromDirectory(File folder) {
        ArrayList fileList = new ArrayList();
        for (File srcFile : folder.listFiles()) {
            try {
                fileList.add(srcFile.getName());
            } catch (Exception e) {
                throw new TapException(TapExceptionType.IO_ERROR, "Not able to get files from directory [{}] ", folder);
            }
        }

        return fileList;
    }

    public static void createFile(String locationPath) {
        File file = new File(locationPath);
        try {
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
            FileUtils.touch(file);

        } catch (Exception e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Not able to delete file from directory [{}] ", locationPath);
        }

    }

    public static void writeToFileUsingBufferWriter(String locationPath, String content) {
        File file = new File(locationPath);
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(content);
//            bw.newLine();
            bw.close();

        } catch (Exception e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Not able to write to file from directory [{}] ", locationPath);
        }

    }

    public static void deleteFile(String locationPath) {
        File file = new File(locationPath);
        try {
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        } catch (Exception e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Not able to delete file from directory [{}] ", locationPath);
        }

    }

    public static String convertFileToString(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "Unable to convert file to string");
        }
        return sb.toString();
    }


}
