package com.performance.demo.performance;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.performance.demo.performance.dao.Gfx;
import com.performance.demo.performance.dao.Network;
import com.performance.demo.utils.parser.GeneralParser;
import com.performance.demo.utils.parser.GfxParser;
import com.performance.demo.utils.parser.NetParser;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.zebrunner.carina.webdriver.IDriverPool;

public class AdbPerformanceCollector extends PerformanceCollector implements IDriverPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String WLAN0 = "wlan0";
    private static final String WLAN1 = "wlan1";

    private final String bundleId = getAppPackage();
    private final GeneralParser generalParser;
    private final String pidCommand = String.format(PerformanceTypes.PID.cmdArgs, bundleId);
    private final String errorOutput = String.format("No process found for: %s%n", bundleId);
    private String cpuCommand;
    private String memCommand;
    private String netCommand;
    private String gfxCommand;

    private Map<String, NetParser.NetRow> netRowStart;
    private Map<String, NetParser.NetRow> netRowEnd;

    private int cpuQuantity = 0;
    private int memQuantity = 0;

    public AdbPerformanceCollector() {
        super();
        this.generalParser = new GeneralParser(bundleId);
        generateCommands();
    }

    public enum PerformanceTypes {
        CPU("ps -p %s -o "),
        MEM("dumpsys meminfo %s | awk '/TOTAL PSS:/ {print $3}'"),
        NET("cat proc/%s/net/dev"),
        PID("pgrep -f %s"),
        CORE("cat /proc/stat"),
        GFX("dumpsys gfxinfo %s framestats");

        private final String cmdArgs;

        PerformanceTypes(String shellCmd) {
            this.cmdArgs = shellCmd;
        }
    }

    @Override
    protected Double collectCpuBenchmarks() {
        String cpuOutput = "";
        double cpuValue = 0D;

        try {
            for (int x = 0; x <= 3; x++) {
                cpuOutput = executeMobileShellCommand(cpuCommand).trim();

                if (!cpuOutput.isEmpty()) {
                    cpuQuantity++;
                    break;
                }
                LOGGER.info("# Attempts for CPU: {}", (x + 1));
            }
        } catch (Exception e) {
            LOGGER.warn("There was an error during executing adb shell command");
        }

        try {
            LOGGER.info("% cpu: {}", cpuOutput);
            cpuValue = Double.parseDouble(cpuOutput);
        } catch (Exception e) {
            LOGGER.warn("There was an error during parsing cpu command output");
        }

        return cpuValue;
    }

    protected Double collectMemoryBenchmarks() {
        String memOutput = "";
        double memValue = 0D;

        try {
            for (int x = 0; x <= 3; x++) {
                memOutput = executeMobileShellCommand(memCommand).trim();

                if (!memOutput.isEmpty() && !errorOutput.equals(memOutput)) {
                    memQuantity++;
                    break;
                }
                LOGGER.info("# Attempts for MEM: {}", (x + 1));
            }
        } catch (Exception e) {
            LOGGER.warn("There was an error during executing adb shell command");
        }

        try {
            LOGGER.info("total pss: {}", memOutput);
            memValue = Double.parseDouble(memOutput);
        } catch (Exception e) {
            LOGGER.warn("There was an error during parsing mem command output");
        }

        return memValue;
    }

    @Override
    protected GfxParser.GfxRow collectGfxBenchmarks() {
        String output = "";

        for (int x = 0; x <= 3; x++) {
            output = executeMobileShellCommand(gfxCommand);
            if (!output.equals(errorOutput)) {
                break;
            }
            LOGGER.info("# Attempts for GFX: {}", (x + 1));
        }

        return (GfxParser.GfxRow) generalParser.parse(Arrays.asList(output.split("\\n")), PerformanceTypes.GFX);
    }

    @Override
    protected void collectNetBenchmarks() {
        String netData = "";

        for (int x = 0; x <= 3; x++) {
            netData = executeMobileShellCommand(netCommand);
            if (!netData.isEmpty()) {
                break;
            }
            LOGGER.info("# Attempts for NET: {}", (x + 1));
        }

        String[] netOutput = netData.split("\\n");

        Map<String, NetParser.NetRow> netRow = generalParser.parseNet(List.of(netOutput));

        try {
            netRow.forEach((type, row) -> LOGGER.info("Net data type: {}, Net Row: {}", type, row));
        } catch (Exception e) {
            LOGGER.warn("There was an error during parsing of netdata");
        }

        if (netRowStart == null) {
            netRowStart = netRow;
        } else {
            netRowEnd = netRow;
        }
    }

    private Network makeSubtraction(NetParser.NetRow rowStart, NetParser.NetRow rowEnd, Instant instant, String flowName) {
        int rbResult = (int) (rowEnd.getRb() - rowStart.getRb());
        int rpResult = (int) (rowEnd.getRp() - rowStart.getRp());
        int tbResult = (int) (rowEnd.getTb() - rowStart.getTb());
        int tpResult = (int) (rowEnd.getTp() - rowStart.getTp());

        return new Network(rbResult, rpResult, tbResult, tpResult, instant, flowName, userName);
    }

    private void subtractNetData(Instant instant, String flowName) {
        try {
            Network resultRow;
            if (netRowStart.size() > 1 && netRowEnd.size() > 1) {
                Network netWlan0 = makeSubtraction(netRowStart.get(WLAN0), netRowEnd.get(WLAN0), instant, flowName);
                Network netWlan1 = makeSubtraction(netRowStart.get(WLAN1), netRowEnd.get(WLAN1), instant, flowName);
                if (netWlan0.getBytesReceived() != 0 && netWlan0.getTransferredBytes() != 0 && netWlan0.getReceivedPackets() != 0
                        && netWlan0.getTransferredPackets() != 0) {
                    resultRow = netWlan0;
                } else {
                    resultRow = netWlan1;
                }
            } else {
                String expectedType;
                if (netRowStart.containsKey(WLAN0) && netRowEnd.containsKey(WLAN0)) {
                    expectedType = WLAN0;
                } else {
                    expectedType = WLAN1;
                }
                resultRow = makeSubtraction(netRowStart.get(expectedType), netRowEnd.get(expectedType), instant, flowName);
            }

            if (resultRow.getBytesReceived() != 0 && resultRow.getTransferredBytes() != 0 && resultRow.getReceivedPackets() != 0
                    && resultRow.getTransferredPackets() != 0) {
                allBenchmarks.add(resultRow);
            } else {
                LOGGER.info("Skipping writing net data to influx because new bucket didn't start");
            }
        } catch (Exception e) {
            LOGGER.warn("No network data was received for the start or the end of the test");
        }
    }

    @Override
    public boolean collectAllBenchmarks(String flowName) {
        Instant instant = Instant.now();

        GfxParser.GfxRow r = collectGfxBenchmarks();

        try {
            LOGGER.info("GFX Row: {}", r);
            allBenchmarks.add(new Gfx(
                    r.getTotalFrames(),
                    r.getJankyFrames(),
                    r.getPercentile90(),
                    r.getPercentile95(),
                    r.getPercentile99(),
                    instant,
                    flowName,
                    userName));
        } catch (Exception e) {
            LOGGER.warn("No data was received for gfx");
        }

        collectNetBenchmarks();
        subtractNetData(instant, flowName);

        boolean isAllDataCollected = false;

        int actionCount;
        if (collectLoginTime && collectExecutionTime) {
            actionCount = loadTimeQty + cpuQuantity + memQuantity + 4;
        } else if (collectLoginTime || collectExecutionTime) {
            actionCount = loadTimeQty + cpuQuantity + memQuantity + 3;
        } else {
            actionCount = loadTimeQty + cpuQuantity + memQuantity + 2;
            LOGGER.warn("No time duration was collected during test execution");
        }
        LOGGER.info("cpuQuantity: " + cpuQuantity + " , memQuantity: " + memQuantity);
        int benchmarkCount = allBenchmarks.size();

        LOGGER.info("Action count: {} benchmark count: {}", actionCount, benchmarkCount);
        if (actionCount == benchmarkCount)
            isAllDataCollected = true;

        return isAllDataCollected;
    }

    private void generateCommands() {
        String pid = executeMobileShellCommand(pidCommand).trim();
        cpuCommand = String.format(PerformanceTypes.CPU.cmdArgs, pid) + "%cpu=";
        netCommand = String.format(PerformanceTypes.NET.cmdArgs, pid);
        memCommand = String.format(PerformanceTypes.MEM.cmdArgs, bundleId);
        gfxCommand = String.format(PerformanceTypes.GFX.cmdArgs, bundleId);
    }

    private String getAppPackage() {
        return ((HasCapabilities) getDriver()).getCapabilities().getCapability("appium:appPackage").toString();
    }

    private String executeMobileShellCommand(String command) {
        return (String) ((JavascriptExecutor) getDriver()).executeScript("mobile: shell",
                ImmutableMap.of("command", "", "args", Collections.singletonList(command)));
    }

}
