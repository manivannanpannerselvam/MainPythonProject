package com.prod.tap.cucumberUtils;


import com.prod.tap.config.Configvariable;
import com.prod.tap.config.Configvariable;
import com.prod.tap.filehandling.CsvUtils;
import com.prod.tap.filehandling.FileReaderUtil;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

@Component
public class SalesFileHelper {

    private Configvariable configvariable = new Configvariable();

    private CsvUtils csvUtils = new CsvUtils();

    public SalesFileHelper() {
    }

    public void loadLocalizationFile(String language, String lbu) {
        String filePath = "/localization/" + lbu + "/" + language + ".csv";
        this.loadCSVDataIntoGlobalMap(filePath, ',');
    }

    public void loadCSVDataIntoGlobalMap(String fileName, char separator) {
        InputStream initialStream = this.getClass().getResourceAsStream(fileName);
        List<String[]> allCsvData = this.csvUtils.csvInputStreamReader(initialStream, separator);
        Iterator var5 = allCsvData.iterator();

        while(var5.hasNext()) {
            String[] row = (String[])var5.next();
            this.configvariable.setStringVariable(row[1], row[0]);
        }

    }

    public void loadTextFileToGlobalMap(InputStream initialStream) {
        String fileToCompare = FileReaderUtil.convertFileToString(initialStream);
        fileToCompare = this.configvariable.expandValue(fileToCompare);
        String[] lines = fileToCompare.split("\n");
        String[] var4 = lines;
        int var5 = lines.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String val = var4[var6];
            if (val.contains("=")) {
                String[] parts = val.split("=");
                String value = "";
                if (parts.length == 1) {
                    value = "";
                } else {
                    value = parts[1];
                }

                this.configvariable.setStringVariable(value, parts[0]);
            }
        }

    }

    public void getSubLBUAndEnv() {
        if (System.getProperty("sales.env").contains("-")) {
            String[] parts = System.getProperty("sales.env").split("-");
            this.configvariable.setStringVariable("/" + parts[0], "sales.sub.lbu");
            this.configvariable.setStringVariable(parts[1], "sales.env");
        } else {
            this.configvariable.setStringVariable(System.getProperty("sales.env"), "sales.env");
        }

    }

    public String encodePass(String password) {
        byte[] encodedBytes = Base64.getEncoder().encode(password.getBytes());
        return new String(encodedBytes);
    }

    public String decodePass(String encodedPass) {
        byte[] decodeBytes = Base64.getDecoder().decode(encodedPass.getBytes());
        return new String(decodeBytes);
    }

    public void loadCommonPropertyFile() {
        InputStream initialStream = this.getClass().getResourceAsStream("/env/Common.properties");
        this.configvariable.propertiesFileLoad(initialStream);
    }
}
