package com.prod.tap.filehandling;

import com.prod.tap.exception.TapException;
import com.prod.tap.exception.TapExceptionType;
import de.redsix.pdfcompare.CompareResult;
import de.redsix.pdfcompare.CompareResultWithPageOverflow;
import de.redsix.pdfcompare.PdfComparator;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfFileUtil {
    private final static Logger logger = Logger.getLogger(PdfFileUtil.class);

    public PDDocument getPDfDocument(String filePath) {
        try {
            logger.info("Loading PDF file: " + filePath);
            return PDDocument.load(new File(filePath));
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "IO Exception while processing file [{}]", filePath);
        }
    }

    public PDDocument getPDfDocument(InputStream filePath) {
        try {
            logger.info("Loading PDF file: " + filePath);
            return PDDocument.load(filePath);
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "IO Exception while processing file [{}]", filePath);
        }
    }

    public String getPDFText(String filePath) {
        try {
            PDDocument document = getPDfDocument(filePath);
            PDFTextStripper textStripper = new PDFTextStripper();
            String pdfText=textStripper.getText(document);
            document.close();
            return pdfText;
        } catch (IOException e) {
            throw new TapException(TapExceptionType.IO_ERROR, "IO Exception while processing file [{}]", filePath);
        }

    }

    public PDPage getPdfPageByPageNumber(String filePath, final Integer pageNumber) {
        InputStream file1InputStream = getClass().getResourceAsStream(filePath);
        PDDocument document = getPDfDocument(file1InputStream);
        if (pageNumber <= 0) {
            logger.error("Page Number cannot be less than or equal to 0");
            throw new TapException(TapExceptionType.INVALID_PARAMETER, "Page Number cannot be less than or equal to 0");
        }
        Integer noOfPages = document.getNumberOfPages();
        if (noOfPages < pageNumber) {
            logger.error("Cannot access page number "+pageNumber+" as it contains "+noOfPages+" number of pages");
            throw new TapException(TapExceptionType.INVALID_PARAMETER, "Cannot access page number [{}] as it contains [{}] pages", pageNumber, noOfPages);
        }
        return document.getPage(pageNumber - 1);
    }

    public String getPdfTextByPageNumberAsInputStream(String filePath, final Integer pageNumber) {
        InputStream file1InputStream = getClass().getResourceAsStream(filePath);
        try (PDDocument pdDocument = this.getPDfDocument(file1InputStream)) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(pageNumber);
            pdfTextStripper.setEndPage(pageNumber);
            String pdfText=pdfTextStripper.getText(pdDocument);
            pdDocument.close();
            return pdfText;
        } catch (IOException | NullPointerException e) {
            logger.error("Exception while processing pdfTextStripper");
            throw new TapException(TapExceptionType.IO_ERROR, "Exception while processing pdfTextStripper", e.getMessage());
        }
    }

    public boolean pdfImageCompareAndWriteDifferenceInPDF(String expectedFile, String actualFile, String exceptionPath) {

        InputStream file1InputStream = getClass().getResourceAsStream(expectedFile);

        InputStream file2InputStream = getClass().getResourceAsStream(actualFile);

        try {
            final CompareResult result = new PdfComparator(file1InputStream, file2InputStream, new CompareResultWithPageOverflow())
                    .compare();

            if (result.isNotEqual()) {
                result.writeTo(exceptionPath);
                return false;
            }
        } catch (IOException e) {
            logger.error("IO Exception while processing comparePdfFilesByImage", e);
            throw new TapException(TapExceptionType.IO_ERROR, "IO Exception while processing comparePdfFilesByImage [{}]", e.getMessage());
        }
        return true;
    }

    public String getPdfTextByPageNumberAsFile(String filePath, final Integer pageNumber) {
        try (PDDocument pdDocument = this.getPDfDocument(filePath)) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(pageNumber);
            pdfTextStripper.setEndPage(pageNumber);
            String pdfText=pdfTextStripper.getText(pdDocument);
            pdDocument.close();
            return pdfText;
        } catch (IOException | NullPointerException e) {
            logger.error("Exception while processing pdfTextStripper");
            throw new TapException(TapExceptionType.IO_ERROR, "Exception while processing pdfTextStripper", e.getMessage());
        }
    }

    public String[] getPDFTextLineByLine(String filePath){
        String pdfText = getPDFText(filePath);
        String[] arrParts=pdfText.split("\n");
        return arrParts;
    }

}
