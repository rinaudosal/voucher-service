package com.docomodigital.delorean.voucher.service.upload;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@FunctionalInterface
public interface VoucherSingleProcessor<A, B, R> {

    R consume(A line, B voucherType, A uploadId);

}
