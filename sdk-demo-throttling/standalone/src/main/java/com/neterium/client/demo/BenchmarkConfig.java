package com.neterium.client.demo;

import com.neterium.client.sdk.instrumentation.TimingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * BenchmarkConfig
 *
 * @author Bernard Ligny
 */
@Configuration
public class BenchmarkConfig {

    @Value("${demo.fake-latency.min:-1}")
    private int latencyMin;

    @Value("${demo.fake-latency.max:-1}")
    private int latencyMax;


    @Bean
    TimingInterceptor timingInterceptor() {
        return new TimingInterceptor(latencyMin, latencyMax);
    }

}