package com.docomodigital.delorean.voucher.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 2020/02/07
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Getter
@Setter
public class VoucherConsumer {

    private String merchantId;
    private String shopId;
    private String requestId;
    private String paymentProvider;
    private String country;
    private String productId;

    private String userId;
    private String transactionId;
    private Instant transactionDate;
    private String billingStatus;


}
