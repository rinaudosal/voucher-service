package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherType;

/**
 * 2020/05/20
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface AccountingService {
    void call(Voucher voucher, VoucherType voucherType, String contractId);

    void call(Voucher voucher, VoucherType voucherType);
}
