package com.prod.tap.tapsteps;

import com.prod.tap.config.Configvariable;
import com.prod.tap.config.TapBeansLoad;
import com.prod.tap.filehandling.FileReaderUtil;
import com.prod.tap.filehandling.PdfFileUtil;
import cucumber.api.DataTable;
import cucumber.api.java.en.When;
import org.junit.Assert;

import java.io.InputStream;
import java.util.List;

public class PdfSteps {

    private PdfFileUtil pdfFileUtil = (PdfFileUtil) TapBeansLoad.getBean(PdfFileUtil.class);
    private Configvariable configvariable = (Configvariable) TapBeansLoad.getBean(Configvariable.class);

    @When("^I verify \"([^\"]*)\" pdf file is matching with \"([^\"]*)\" pdf file and exceptions are written in \"([^\"]*)\"$")
    public void verifyPDFDocuments(String pdfFile1, String pdfFile2, String outfile1Path) {
        boolean match=pdfFileUtil.pdfImageCompareAndWriteDifferenceInPDF(configvariable.expandValue(pdfFile1),configvariable.expandValue(pdfFile2),configvariable.expandValue(outfile1Path));
        Assert.assertTrue("PDF files are matched",match);
    }

    @When("^I verify PDF file \"([^\"]*)\" should contain following values$")
    public void verifyPDFText(String pdfFile1, DataTable pdfContents) {
        String fileContent=pdfFileUtil.getPdfTextByPageNumberAsInputStream(configvariable.expandValue(pdfFile1),1);
        List<String> pdfCon;
        pdfCon = pdfContents.asList(String.class);
        for(String data : pdfCon){
            Assert.assertTrue(fileContent.contains(configvariable.expandValue(data)));
        }
    }

    @When("^I verify PDF file \"([^\"]*)\" should contain following values in page number (.*)$")
    public void verifyPDFTextInPage(String pdfFile1, int pageNumber,DataTable pdfContents) {
        String fileContent=pdfFileUtil.getPdfTextByPageNumberAsInputStream(configvariable.expandValue(pdfFile1),pageNumber);
        List<String> pdfCon;
        pdfCon = pdfContents.asList(String.class);
        for(String data : pdfCon){
            Assert.assertTrue(fileContent.contains(configvariable.expandValue(data)));
        }
    }

    @When("^I verify downloaded PDF file \"([^\"]*)\" should contain following values$")
    public void verifyDownloadedPDFText(String pdfFile1, DataTable pdfContents) {
        String fileContent=pdfFileUtil.getPDFText(configvariable.expandValue(pdfFile1));
        List<String> pdfCon;
        pdfCon = pdfContents.asList(String.class);
        for(String data : pdfCon){
            Assert.assertTrue(fileContent.contains(configvariable.expandValue(data)));
        }

    }

    @When("^I verify downloaded PDF file \"([^\"]*)\" should contain following values in page number (.*)$")
    public void verifyDownloadedPDFTextInPage(String pdfFile1, int pageNumber,DataTable pdfContents) {
        String fileContent=pdfFileUtil.getPdfTextByPageNumberAsFile(configvariable.expandValue(pdfFile1),pageNumber);
        List<String> pdfCon;
        pdfCon = pdfContents.asList(String.class);
        for(String data : pdfCon){
            Assert.assertTrue(fileContent.contains(configvariable.expandValue(data)));
        }
    }

    @When("^I verify downloaded PDF file \"([^\"]*)\" should contain the values in file \"([^\"]*)\"$")
    public void verifyDownloadedPDFText(String pdfFile1, String fileToCheck) {
        String fileContent = this.pdfFileUtil.getPDFText(this.configvariable.expandValue(pdfFile1));
        InputStream initialStream1 = this.getClass().getResourceAsStream(this.configvariable.expandValue(fileToCheck));
        String fileToCompare = FileReaderUtil.convertFileToString(initialStream1);
        fileToCompare=configvariable.expandValue(fileToCompare);
        String[] lines=fileToCompare.split("\n");
        for(String val:lines){
            Assert.assertTrue(val+ " is present in PDF file", fileContent.contains(val));
        }

    }


}
