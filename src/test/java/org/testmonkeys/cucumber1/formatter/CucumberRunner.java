package org.testmonkeys.cucumber1.formatter;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Unit test for simple App.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"org.testmonkeys.cucumber1.formatter"},
        format = {
                "org.testmonkeys.cucumber1.formatter.CucumberLogsFormatter",
                "org.testmonkeys.cucumber.ext.formatters.json.PerFeatureFormatter:target/json-report",
        }
)
public class CucumberRunner {

}
