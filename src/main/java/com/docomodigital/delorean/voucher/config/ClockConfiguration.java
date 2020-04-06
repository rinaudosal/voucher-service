package com.docomodigital.delorean.voucher.config;

/**
 * Manage the clock based on springboot profiles
 * 2020/01/22
 *
 * @author salvatore.rinaudo@docomodigital.com
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Clock configuration for test purpose
 */
@Configuration
public class ClockConfiguration {

    /**
     * Creates application clock
     *
     * @return The clock
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
