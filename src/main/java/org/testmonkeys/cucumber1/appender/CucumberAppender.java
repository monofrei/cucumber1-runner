package org.testmonkeys.cucumber1.appender;

import ch.qos.logback.core.AppenderBase;

public class CucumberAppender extends AppenderBase {

    @Override
    protected void append(Object o) {
        CucumberScenarioContext.getInstance().getScenario().write(o.toString());
    }
}
