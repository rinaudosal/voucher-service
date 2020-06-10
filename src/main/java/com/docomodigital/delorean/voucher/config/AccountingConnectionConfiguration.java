package com.docomodigital.delorean.voucher.config;

import com.docomodigital.delorean.client.merchant.MerchantClient;
import com.docomodigital.delorean.voucher.repository.VoucherErrorRepository;
import com.docomodigital.delorean.voucher.service.AccountingService;
import com.docomodigital.delorean.voucher.service.AccountingServiceImpl;
import com.docomodigital.delorean.voucher.service.MockedAccountingService;
import lombok.extern.slf4j.Slf4j;
import net.netm.billing.library.AccountingConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;

/**
 * 2020/05/14
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Configuration
public class AccountingConnectionConfiguration {

    @Bean
    @Profile("!test")
    public AccountingConnection accountingConnection() {
        return new AccountingConnection();
    }

    @Bean
    @Profile({"stg","prod"})
    public AccountingService realAccountingService(Clock clock,
                                                   VoucherErrorRepository voucherErrorRepository,
                                                   AccountingConnection accountingConnection,
                                                   MerchantClient merchantClient){
        log.info("build realAccountingService instance");
        return new AccountingServiceImpl(clock, voucherErrorRepository, accountingConnection, merchantClient);
    }

    @Bean
    @Profile("!stg & !prod")
    public AccountingService mockAccountingService(){
        log.info("build mockAccountingService instance");
        return new MockedAccountingService();
    }

}
