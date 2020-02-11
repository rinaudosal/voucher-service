package com.docomodigital.delorean.voucher.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 2020/02/07
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Getter
@Setter
@NoArgsConstructor
public class VoucherConsumer {

    private String merchantId;
    private String paymentProvider;
    private String country;
    private String productId;

    private String userId;
    private String transactionId;
    private LocalDateTime transactionDate;
    private String billingStatus;

}
