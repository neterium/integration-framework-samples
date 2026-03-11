package com.neterium.client.demo.batch.feeding;

import com.neterium.client.demo.files.DataFile;
import com.neterium.client.sdk.batch.support.LinearPartitioner;
import com.neterium.client.sdk.files.FileService;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.neterium.client.demo.batch.support.JobParamsSupport.PARAM_DATA_FILE;

/**
 * CounterPartFilePartitioner
 *
 * @author Bernard Ligny
 */
@Component
@JobScope
public class CounterPartFilePartitioner extends LinearPartitioner {

    private static final boolean FIRST_LINE_IS_HEADER = true;

    @Value("${demo.jobs.min-partition-size:500}")
    private int minPartitionSize;


    public CounterPartFilePartitioner(@Value("#{jobParameters}") Map<String, Object> jobParameters,
                                      FileService fileService) {
        super(CounterPartFileReader.class);
        var path = jobParameters.get(PARAM_DATA_FILE).toString();
        var dataFile = DataFile.from(path);
        super.setItemCountProvider(() -> fileService.countLines(dataFile.getRunningFile()));
    }


    @Override
    protected int getInitialOffset() {
        return (FIRST_LINE_IS_HEADER ? 1 : 0);
    }

    @Override
    protected int getMinPartitionSize() {
        return minPartitionSize;
    }

}
