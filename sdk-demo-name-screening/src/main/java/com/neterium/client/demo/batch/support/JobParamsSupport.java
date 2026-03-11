package com.neterium.client.demo.batch.support;

import com.neterium.client.demo.files.DataFile;
import org.springframework.batch.core.JobParameters;

import java.util.Objects;

/**
 * Wrapper around JobParameters to ease parameter extraction
 *
 * @author Bernard Ligny
 */
public class JobParamsSupport {

    public static final String PARAM_DATA_FILE = "dataFile";
    public static final String PARAM_SCREENING_COLLECTION = "screeningCollection";
    public static final String PARAM_SCREENING_THRESHOLD = "screeningThreshold";
    public static final String PARAM_SCREEN_AFTER_IMPORT = "chainWithScreening";
    private final JobParameters jobParameters;


    public JobParamsSupport(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }

    public DataFile getDataFile() {
        var path = jobParameters.getString(PARAM_DATA_FILE);
        Objects.requireNonNull(path);
        return DataFile.from(path);
    }

    public String getScreeningCollection() {
        return jobParameters.getString(PARAM_SCREENING_COLLECTION);
    }

    public int getScreeningThreshold() {
        var threshold = jobParameters.getLong(PARAM_SCREENING_THRESHOLD);
        Objects.requireNonNull(threshold);
        return threshold.intValue();
    }

}
