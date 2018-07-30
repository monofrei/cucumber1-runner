package org.testmonkeys.cucumber1.formatter;


import cucumber.runtime.StepDefinitionMatch;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.testmonkeys.cucumber1.formatter.Status.*;

public class CucumberLogsFormatter implements Formatter, Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Feature currentFeature;
    private String featureUri;
    private String currentStep;
    private Status testStatus;
    private Result failedResult;
    private long scenarioDuration;
    private long stepStarted;

    @Override
    public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {

    }

    @Override
    public void uri(String s) {
        featureUri = s;
    }

    @Override
    public void feature(Feature feature) {
        if (currentFeature != null) {
            logger.info("FEATURE FINISHED:" + feature.getName());
        }
        logger.info("FEATURE STARTED:" + feature.getName() + "[" + featureUri + "]");
        currentFeature = feature;
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        logger.info("[EXECUTING SCENARIO OUTLINE]:" + scenarioOutline.getName());
    }

    @Override
    public void examples(Examples examples) {
        logger.info("[EXAMPLES]:" + System.lineSeparator() + displayExamples(examples.getRows()));
    }

    private StringBuilder displayExamples(List<ExamplesTableRow> examples) {
        final StringBuilder builder = new StringBuilder();
        for (ExamplesTableRow row : examples) {
            builder.append("|");
            row.getCells().forEach(c -> builder.append(c).append("|"));
            builder.append(System.lineSeparator());
        }
        return builder;
    }

    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        String name = scenario.getName();
        int line = scenario.getLine();
        TestLogHelper.stopTestLogging();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

        TestLogHelper.startTestLogging(format.format(new Date().getTime()) + "_" + name + "_" + line);
        logger.info("[TEST STARTED]: " + name + " at line[" + line + "]");
    }

    @Override
    public void background(Background background) {
        logger.info("[BACKGROUND]:" + background.getName() + System.lineSeparator());
    }

    @Override
    public void scenario(Scenario scenario) {

    }

    @Override
    public void step(Step step) {

    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {
        if (failedResult != null)
            logger.error("[TEST " + testStatus + "][" + scenarioDuration + "]:" + scenario.getName(), failedResult.getError());
        else
            logger.info("[TEST " + testStatus + "][" + scenarioDuration + "]:" + scenario.getName() + System.lineSeparator());

        scenarioDuration = 0;
        currentStep = null;
        failedResult = null;

    }

    @Override
    public void done() {

    }

    @Override
    public void close() {

    }

    @Override
    public void eof() {

    }

    @Override
    public void before(Match match, Result result) {
        Status status = Status.valueOf(result.getStatus().toUpperCase());
        Long duration = TimeUnit.MILLISECONDS.toSeconds(result.getDuration());
        if (status.equals(FAILED))
            logger.error("[BEFORE HOOK FAILED][" + duration + "]:" + match.getLocation(), result.getError());
        else
            logger.info("[BEFORE HOOK " + status + "][" + duration + "]:" + match.getLocation());
    }

    @Override
    public void result(Result result) {
        Status status = Status.valueOf(result.getStatus().toUpperCase());
        long duration = calculateDuration(stepStarted, System.currentTimeMillis(), TimeUnit.SECONDS);
        if (status.equals(FAILED)) {
            failedResult = result;
            logger.error("[STEP FAILED][" + duration + "]:" + currentStep, result.getError());
        } else
            logger.info("[STEP " + result.getStatus().toUpperCase() + "][" + duration + "]:" + currentStep + System.lineSeparator());

        applyTestStatus(Status.valueOf(result.getStatus().toUpperCase()));
        scenarioDuration += duration;
    }

    private void applyTestStatus(Status newStatus) {
        if (Objects.equals(testStatus, FAILED)) return;
        if (Objects.equals(testStatus, PASSED) && newStatus.equals(SKIPPED)) return;
        testStatus = newStatus;
    }

    @Override
    public void after(Match match, Result result) {
        Status status = Status.valueOf(result.getStatus().toUpperCase());
        if (status.equals(FAILED))
            logger.error("[AFTER HOOK FAILED]:" + match.getLocation(), result.getError());
        else
            logger.info("[AFTER HOOK " + status + "]:" + match.getLocation());
    }

    @Override
    public void match(Match match) {
        if (match instanceof StepDefinitionMatch) {
            currentStep = ((StepDefinitionMatch) match).getStepLocation().getMethodName();
            logger.info("[STEP STARTED]:" + currentStep);
            stepStarted = System.currentTimeMillis();
        }
    }

    @Override
    public void embedding(String mimeType, byte[] data) {

    }

    @Override
    public void write(String text) {

    }

    private long calculateDuration(long started, long finished, TimeUnit outputUnit) {
        long duration = finished - started;
        return outputUnit.convert(duration, TimeUnit.MILLISECONDS);
    }
}
