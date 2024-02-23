package com.performance.demo.utils.parser;

import java.util.List;
import java.util.Map;

import com.performance.demo.performance.AdbPerformanceCollector;
import com.zebrunner.carina.webdriver.device.Device;

public class GeneralParser {

    private final String bundleId;

    public GeneralParser(String bundleId) {
        this.bundleId = bundleId;
    }

    public Row parse(List<String> lines, AdbPerformanceCollector.PerformanceTypes performanceTypes) {
        switch (performanceTypes) {
            case GFX: {
                return new GfxParser().parse(lines);
            }
            default:
                return new Row() {
                };
        }
    }

    public Map<String, NetParser.NetRow> parseNet(List<String> lines) {
        return new NetParser2().parseForTwoTypes(lines);
    }

    public String getBundleId() {
        return bundleId;
    }

}