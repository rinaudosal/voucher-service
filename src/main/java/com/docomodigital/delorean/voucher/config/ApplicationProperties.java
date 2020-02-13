package com.docomodigital.delorean.voucher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Voucher.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 *
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String env;
    private String endpoint;
    private String inputQueueName;
    private String outputQueueName;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getInputQueueName() {
        return inputQueueName;
    }

    public void setInputQueueName(String inputQueueName) {
        this.inputQueueName = inputQueueName;
    }

    public String getOutputQueueName() {
        return outputQueueName;
    }

    public void setOutputQueueName(String outputQueueName) {
        this.outputQueueName = outputQueueName;
    }
}
