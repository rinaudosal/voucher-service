package com.docomodigital.delorean.voucher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 2020/02/07
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
