package com.neterium.client.demo.batch.feeding;

import com.neterium.client.demo.batch.support.JobParamsSupport;
import com.neterium.client.demo.files.CsvCounterPart;
import com.neterium.client.sdk.batch.support.LinearPartitioner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CounterPartFileReader
 *
 * @author Bernard Ligny
 */
@Component
@StepScope
@Slf4j
public class CounterPartFileReader extends FlatFileItemReader<CsvCounterPart> {

    private static final String[] FIELD_NAMES = new String[]{
            "recordId", "type", "lastName", "firstName", "middleNames", "gender", "dateOfBirth",
            "registrationCountryCode", "registrationNumber", "addressCountryCode", "addressCityName"
    };


    /**
     * Static part of reader configuration
     */
    public CounterPartFileReader() {
        super();
        super.setName(this.getClass().getSimpleName());
        super.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        super.setLineMapper(configureLineMapper());
    }


    /**
     * Dynamic part of reader configuration
     */
    @BeforeStep
    public void configureReader(StepExecution stepExecution) {
        var jobParams = new JobParamsSupport(stepExecution.getJobParameters());
        var csvFile = jobParams.getDataFile().getRunningFile();
        super.setResource(new FileSystemResource(csvFile));
        var offset = getPartitionParam(stepExecution, LinearPartitioner.KEY_OFFSET, Integer.class);
        super.setLinesToSkip(offset);
        var maxLines = getPartitionParam(stepExecution, LinearPartitioner.KEY_MAX_ITEMS, Integer.class);
        super.setMaxItemCount(maxLines);
        log.debug("About to read {} records starting from row#{}", maxLines, offset);
    }


    private <T> T getPartitionParam(StepExecution stepExecution, String paramName, Class<T> paramType) {
        String suffix = this.getClass().getSimpleName();
        return stepExecution.getExecutionContext().get(suffix + paramName, paramType);
    }


    private LineMapper<CsvCounterPart> configureLineMapper() {
        var tokenizer = new NullAwareTokenizer();
        tokenizer.setNames(FIELD_NAMES);
        tokenizer.setDelimiter(",");
        tokenizer.setQuoteCharacter('\"');
        tokenizer.setStrict(false);
        var filedMapper = new BeanWrapperFieldSetMapper<CsvCounterPart>();
        filedMapper.setTargetType(CsvCounterPart.class);
        var lineMapper = new DefaultLineMapper<CsvCounterPart>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(filedMapper);
        return lineMapper;
    }


    /*
     * DelimitedLineTokenizer implementation
     * which accepts null values
     */
    private static class NullAwareTokenizer extends DelimitedLineTokenizer {
        @Override
        protected List<String> doTokenize(String line) {
            return super.doTokenize(line)
                    .stream()
                    .map(v -> (ObjectUtils.isEmpty(v) ? null : v))
                    .collect(Collectors.toList());
        }
    }


}
