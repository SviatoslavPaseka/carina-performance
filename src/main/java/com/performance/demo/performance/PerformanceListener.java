package com.performance.demo.performance;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.events.WebDriverListener;

import com.google.common.base.Stopwatch;
import com.zebrunner.carina.utils.commons.SpecialKeywords;
import com.zebrunner.carina.webdriver.config.WebDriverConfiguration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public class PerformanceListener implements WebDriverListener {

    private static PerformanceCollector performanceCollector;
    private static String flowName;

    /**
     * This method should be used in the beginning of each performance test
     */
    public static void startPerformanceTracking(String flowName, String userName, boolean isCollectLoginTime, boolean isCollectExecutionTime) {
        if (!SpecialKeywords.IOS.equalsIgnoreCase(WebDriverConfiguration.getCapability(CapabilityType.PLATFORM_NAME).orElseThrow())) {
            performanceCollector = new AdbPerformanceCollector();
            performanceCollector.setUserName(userName);
            setFlowName(flowName);
            performanceCollector.setLoadTimeStopwatch(Stopwatch.createUnstarted());
            performanceCollector.setCollectLoginTime(isCollectLoginTime);
            performanceCollector.setCollectExecutionTime(isCollectExecutionTime);
            startTracking();
        }
    }

    /**
     * This method is used in authService.loginByUsernameWithPerf
     */
    public static void collectLoginTime() {
        if (flowName != null && (performanceCollector.isCollectLoginTime() && performanceCollector.isCollectExecutionTime()))
            performanceCollector.collectLoginTime(flowName);
    }

    /**
     * This method should be used in the end of each performance test
     */
    public static void stopPerformanceTracking() {
        if (flowName != null) {
            if (performanceCollector.isCollectLoginTime() && !performanceCollector.isCollectExecutionTime())
                performanceCollector.collectLoginTime(flowName);
            else if (performanceCollector.isCollectExecutionTime())
                performanceCollector.collectExecutionTime(flowName);

            performanceCollector.collectAndWritePerformance(flowName);
        }
    }

    @Override
    public void afterClick(WebElement element) {
        String action = "Clicking " + Arrays.stream(element.toString().split("->"))
                .reduce((first, second) -> second.substring(0, second.length() - 1))
                .orElse("empty");
        if (flowName != null) {
            performanceCollector.collectSnapshotBenchmarks(flowName, action);
        }
        if (!performanceCollector.getLoadTimeStopwatch().isRunning()) {
            performanceCollector.setLoadTimeStopwatch(Stopwatch.createStarted());
            performanceCollector.setClickActionName(action);
        }
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        String action = "Typing: " + Arrays.toString(keysToSend) +
                ", element: " + Arrays.stream(element.toString().split("->"))
                .reduce((first, second) -> second.substring(0, second.length() - 1))
                .orElse("empty");
        if (flowName != null)
            performanceCollector.collectSnapshotBenchmarks(flowName, action);
    }

    @Override
    public void afterPerform(WebDriver driver, Collection<Sequence> actions) {
        String action = "Swiping";
//        actions.forEach();
        if (flowName != null)
            performanceCollector.collectSnapshotBenchmarks(flowName, action);
    }
    @Override
    public void beforeAnyWebElementCall(WebElement element, Method method, Object[] args) {
        if (performanceCollector.getLoadTimeStopwatch().isRunning()) {
            performanceCollector.collectLoadTime(flowName);
        }
    }

    private static void startTracking() {
        if (flowName != null) {
            if (performanceCollector.isCollectLoginTime() && performanceCollector.isCollectExecutionTime()) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                performanceCollector.setLoginStopwatch(stopwatch);
                performanceCollector.setExecutionStopWatch(stopwatch);
            } else if (performanceCollector.isCollectLoginTime())
                performanceCollector.setLoginStopwatch(Stopwatch.createStarted());
            else if (performanceCollector.isCollectExecutionTime())
                performanceCollector.setExecutionStopWatch(Stopwatch.createStarted());

            performanceCollector.collectNetBenchmarks();
        }
    }

    public static PerformanceCollector getPerformanceCollector() {
        return performanceCollector;
    }

    public static void setPerformanceCollector(PerformanceCollector performanceCollector) {
        PerformanceListener.performanceCollector = performanceCollector;
    }

    public static String getFlowName() {
        return flowName;
    }

    public static void setFlowName(String flowName) {
        PerformanceListener.flowName = flowName;
    }
}
