package com.neterium.client.demo.controllers;

import com.neterium.client.demo.instrumentation.DashboardWidget;
import com.neterium.client.demo.instrumentation.DashboardWidget.ThresholdName;
import com.neterium.client.sdk.batch.metrics.ExecutionInfo;
import com.neterium.client.sdk.batch.metrics.LastJobInspector;
import com.neterium.client.sdk.instrumentation.Measurable;
import com.neterium.client.sdk.screening.CounterpartScreener;
import com.neterium.client.sdk.screening.TransactionScreener;
import com.neterium.client.sdk.throttling.ThrottlerImpl;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DashboardController
 *
 * @author Bernard Ligny
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private static final String UNIT_RECORDS = "records";
    private static final String UNIT_MS = "ms";
    private static final String UNIT_BATCHES = "batches";
    private static final String UNIT_REQUEST = "requests";
    private static final String UNIT_PERCENTS = "%";
    private static final String UNIT_THREADS = "threads";

    private static final String UNIT_SECONDS = "seconds";
    private static final String UNIT_OCCURRENCES = "occurrences";
    private static final String UNIT_DURATION = "duration";


    private final Environment environment;
    private final Map<Class<? extends Measurable>, List<DashboardWidget>> displayedMetrics;
    private final Set<Measurable> measuredComponents;
    private final LastJobInspector jobInspector;


    public DashboardController(Environment environment,
                               Set<Measurable> measurables,
                               LastJobInspector jobInspector) {
        this.environment = environment;
        this.measuredComponents = measurables;
        this.jobInspector = jobInspector;
        this.displayedMetrics = configureMetrics();
    }

    @GetMapping(value = "/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Collection<DashboardWidget>> fetchMetrics() {
        return measuredComponents
                .stream()
                .filter(component -> displayedMetrics.containsKey(component.getClass()))
                .collect(Collectors.toMap(Measurable::getCategoryName, this::fetchComponentMetrics));
    }

    @GetMapping(value = "/last-job", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExecutionInfo> lastJobExecution() {
        var results = jobInspector.getExecutionResults();
        // In chronological order
        results.sort(Comparator.comparing(
                ExecutionInfo::start,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));
        return results;
    }

    private Collection<DashboardWidget> fetchComponentMetrics(Measurable component) {
        var metrics = displayedMetrics.get(component.getClass());
        metrics.forEach(m -> populate(m, component.describe()));
        return metrics;
    }

    private void populate(DashboardWidget metric, Map<String, Object> properties) {
        metric.setValue((Long) properties.get(metric.getName()));
        if ("metric-value".equals(metric.getName())) {
            // Add thresholds info for gauge
            metric.setIsGauge(true);
            metric.setLabel(properties.get("metric-name") + " Response Time");
            metric.setThresholds(
                    Map.of(ThresholdName.T0, (Long) properties.get("min-value"),
                            ThresholdName.T1, (Long) properties.get("lower-limit"),
                            ThresholdName.T2, (Long) properties.get("upper-limit"),
                            ThresholdName.TM, (Long) properties.get("max-value")
                    )
            );
        }
    }

    private Map<Class<? extends Measurable>, List<DashboardWidget>> configureMetrics() {
        var map = new HashMap<Class<? extends Measurable>, List<DashboardWidget>>();
        map.put(ThrottlerImpl.class,
                List.of(
                        new DashboardWidget("value-count", "API Requests", UNIT_REQUEST),
                        new DashboardWidget("timeouts", "Timeouts", UNIT_OCCURRENCES),
                        new DashboardWidget("metric-value", "(dynamic)", UNIT_MS),
                        new DashboardWidget("throttler-value", "Pool Size", UNIT_THREADS),
                        new DashboardWidget("window-size", "Window Size", UNIT_SECONDS)
                ));
        map.put(LastJobInspector.class,
                List.of(
                        new DashboardWidget("job-progress", "Job Progress", UNIT_PERCENTS),
                        new DashboardWidget("job-duration", "Job Duration", UNIT_DURATION),
                        new DashboardWidget("job-read-count", "Read", UNIT_RECORDS),
                        new DashboardWidget("job-write-count", "Processed", UNIT_RECORDS)
                ));
        if (environment.acceptsProfiles(Profiles.of("jetscan"))) {
            map.put(CounterpartScreener.class,
                    List.of(
                            new DashboardWidget("count-records", "Counterparts", UNIT_RECORDS),
                            new DashboardWidget("count-requests", "API Requests", UNIT_BATCHES),
                            new DashboardWidget("time-average", "Average Response Time", UNIT_MS),
                            new DashboardWidget("time-p90", "Percentile 90", UNIT_MS)
                    ));
        }
        if (environment.acceptsProfiles(Profiles.of("jetflow"))) {
            map.put(TransactionScreener.class,
                    List.of(
                            new DashboardWidget("count-records", "Transactions", UNIT_RECORDS),
                            new DashboardWidget("count-requests", "API Requests", UNIT_BATCHES),
                            new DashboardWidget("time-average", "Average Response Time", UNIT_MS),
                            new DashboardWidget("time-p90", "Percentile 90", UNIT_MS)
                    ));
        }
        return map;
    }

}
