package com.prod.tap.filehandling;


import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class CsvUtils {

    private static final Logger logger = Logger.getLogger(CsvUtils.class);

    public List<String[]> readAlldataFromCSVFile(String fileName, char separator) {
        List<String[]> allData = null;
        try {
            FileReader filereader = new FileReader(new File(getClass().getClassLoader().getResource(fileName).getFile()));
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to load all data from csv file [{}]", fileName);
        }
        return allData;
    }


    public String[] readCSVFileHeaders(String fileName) {
        String[] headers = null;
        try {
            FileReader filereader = new FileReader(new File(getClass().getClassLoader().getResource(fileName).getFile()));
            //fetch first header line
            CSVReader csvReader = new CSVReader(filereader);
            headers = csvReader.readNext();
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to load all data from csv file [{}]", fileName);
        }
        return headers;
    }

    public List<String[]> csvInputStreamReader(InputStream inputStream, char separator) {
        List<String[]> allData = null;
        try {
//            FileReader filereader = new FileReader(new File(getClass().getClassLoader().getResource(fileName).getFile()));
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            throw new TapException(TapExceptionType.PROCESSING_FAILED, "Not able to load all data from csv file [{}]", e.getMessage());
        }
        return allData;
    }


}
