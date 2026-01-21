package com.prod.tap.tapsteps;

import com.prod.tap.config.Configvariable;
import com.prod.tap.config.TapBeansLoad;
import com.prod.tap.filehandling.FileReaderUtil;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

import java.io.InputStream;
import java.util.Map;
import java.util.Random;

public class ConfigvariableSteps {
    private Configvariable configvariable = (Configvariable) TapBeansLoad.getBean(Configvariable.class);

    @And("I assign \"([^\"]*)\" to variable \"([^\"]*)\"")
    public void assignValueToVariable(String value, String Variable) {
        configvariable.assignValueToVar(value, Variable);
    }

    @When("I generate random number and assign to variable \"([^\"]*)\"")
    public void generateRandomNumberAndAssignToVariable(String varName) {
        Random rand = new Random();
        int random_num = rand.nextInt(1000);
        String num = Integer.toString(random_num);
        configvariable.setStringVariable(num + configvariable.generateRandomNumber("ddMMHMs"), varName);
    }

    @When("I assign value to following variables")
    public void assignValueToVariables(DataTable userDetails) {
        Map<String, String> variableMap;
        variableMap = userDetails.asMap(String.class, String.class);
        configvariable.assignValueToVarMap(variableMap);
    }

    @When("I calculate age of the user is (.*) in \"([^\"]*)\" format from current date and assign to variable \"([^\"]*)\"")
    public void calculateAge(int age, String format, String varName) {
        configvariable.setStringVariable(configvariable.minusYearFromCurrentDate(age, format), varName);
    }

    @When("I generate time in format \"([^\"]*)\" and assign to variable \"([^\"]*)\"")
    public void generateDateAndTime(String format, String varName) {
        configvariable.setStringVariable(configvariable.generateRandomNumber(format), varName);
    }

    @When("I expect the value of var \"([^\"]*)\" equals to \"([^\"]*)\"")
    public void verifyStringMatches(String value1, String value2) {
        configvariable.match(configvariable.expandValue(value1), configvariable.expandValue(value2));
    }

    @Given("^I copy the csv template \"([^\"]*)\" and replace following variables in output path \"([^\"]*)\"$")
    public void replaceParamsInCSVFileAndGenerateOutputFile(String inputPath, String outputPath, DataTable variables) {
        Map<String, String> variableMap;
        variableMap = variables.asMap(String.class, String.class);
        configvariable.assignValueToVarMap(variableMap);
        InputStream initialStream = this.getClass().getResourceAsStream(configvariable.expandValue(inputPath));
        String inputString = FileReaderUtil.convertFileToString(initialStream);
        inputString = inputString.substring(0, inputString.length() - 1);
        String outPath = configvariable.getBaseDirectory() + "/src/test/resources" + configvariable.expandValue(outputPath);
        configvariable.setStringVariable(outPath, "OUTPUT_PATH");
        FileReaderUtil.deleteFile(outPath);
        FileReaderUtil.writeToFileUsingBufferWriter(outPath, configvariable.expandValue(inputString));
    }

    @Given("I assign the downloaded file \"([^\"]*)\" to variable \"([^\"]*)\"")
    public void assignDownloadedFilePathToVariable(String fileName,String path){
        String fileAbsPath = configvariable.getBaseDirectory()+"\\"+configvariable.expandValue(fileName);
        configvariable.setStringVariable(fileAbsPath.replace("\\","/"),path);
    }

    @Given("^I delete the downloaded file \"([^\"]*)\" if it already exists$")
    public void deleteDownloadedFile(String filePath){
        FileReaderUtil.deleteFile(configvariable.expandValue(filePath));
    }

}
