package com.neterium.client.demo.batch;

import org.springframework.batch.core.JobParameters;

import java.util.Objects;

/**
 * Wrapper around JobParameters to ease parameter extraction
 *
 * @author Bernard Ligny
 */
public class ScreeningParameters {

    public static final String PARAM_SCREENING_COLLECTION = "screeningCollection";
    public static final String PARAM_SCREENING_THRESHOLD = "screeningThreshold";
    public static final String PARAM_GENERATE_RECORD_COUNT = "generatorRecordCount";
    public static final String PARAM_GENERATE_HIT_RATIO = "generatorHitRatio";
    public static final String PARAM_BATCH_SIZE = "batchSize";

    private final JobParameters jobParameters;


    public ScreeningParameters(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }


    public String getScreeningCollection() {
        return jobParameters.getString(PARAM_SCREENING_COLLECTION);
    }

    public int getScreeningThreshold() {
        var threshold = jobParameters.getLong(PARAM_SCREENING_THRESHOLD);
        Objects.requireNonNull(threshold);
        return threshold.intValue();
    }

    public double getHitRatio() {
        var ratio = jobParameters.getDouble(PARAM_GENERATE_HIT_RATIO);
        Objects.requireNonNull(ratio);
        return ratio;
    }

    public int getBatchSize() {
        var threshold = jobParameters.getLong(PARAM_BATCH_SIZE);
        Objects.requireNonNull(threshold);
        return threshold.intValue();
    }

}
