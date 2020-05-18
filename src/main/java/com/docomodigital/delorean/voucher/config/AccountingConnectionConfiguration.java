package com.docomodigital.delorean.voucher.config;

import net.netm.billing.library.AccountingConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 2020/05/14
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Configuration
public class AccountingConnectionConfiguration {

    @Bean
    @Profile("!test")
    public AccountingConnection accountingConnection() {
        return new AccountingConnection();
    }

}
