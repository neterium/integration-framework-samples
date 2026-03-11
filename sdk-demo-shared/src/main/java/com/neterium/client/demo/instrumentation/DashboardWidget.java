package com.neterium.client.demo.instrumentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;

/**
 * DashboardWidget
 *
 * @author Bernard Ligny
 */
@Data
public class DashboardWidget {

    @NonNull
    private String name;

    @NonNull
    private String label;

    @NonNull
    private String unit;

    @JsonProperty("isGauge")
    private Boolean isGauge = false;

    private Long value;

    private Map<ThresholdName, Long> thresholds;

    public enum ThresholdName {
        T0, // leftmost/min value
        T1, // first limit
        T2, // second limit
        TM  // max value
    }

}
