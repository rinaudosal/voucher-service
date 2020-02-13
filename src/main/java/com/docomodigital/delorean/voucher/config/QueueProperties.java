package com.docomodigital.delorean.voucher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Voucher.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "queue", ignoreUnknownFields = false)
public class QueueProperties {
    private String inputQueueName;
    private String outputQueueName;

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
