package com.prod.tap.cucumberUtils;

import cucumber.api.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ScenarioUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioUtils.class);

    private Scenario scenario;

    public Scenario getScenario() {
        return scenario;
    }

    public void setCurrentScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void write(final String data) {
        try {
            if (scenario != null) {
                scenario.write(data);
            }
        } catch (Exception e) {
            //ignore
        }

    }

    public void embed(final byte[] bytes, final String var1) {
        if (scenario != null) {
            scenario.embed(bytes, var1);
        }
    }

    public ArrayList<String> getTagNames() {
        if (scenario != null) {
            return (ArrayList<String>) scenario.getSourceTagNames();
        }
        return new ArrayList<>();
    }

    public boolean isTagPresent(final String tag) {
        return getTagNames().contains(tag);
    }

    public boolean isScenarioFailed() {
        return scenario != null && scenario.isFailed();
    }

    public String getName() {
        if (scenario != null) {
            return scenario.getName();
        }
        return "";
    }

}
