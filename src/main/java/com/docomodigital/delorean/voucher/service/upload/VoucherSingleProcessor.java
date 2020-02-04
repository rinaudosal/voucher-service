package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherType;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@FunctionalInterface
public interface VoucherSingleProcessor<A extends String, B extends VoucherType, C extends String, R extends Voucher> {

    R consume(A line, B voucherType, C uploadId);

}
