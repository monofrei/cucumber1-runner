package org.testmonkeys.cucumber1.formatter;

public class ScenarioContext {

    private static ScenarioContext instance;

    private ScenarioContext() {

    }

    public static ScenarioContext getInstance() {
        if (instance == null)
            instance = new ScenarioContext();
        return instance;
    }
}
