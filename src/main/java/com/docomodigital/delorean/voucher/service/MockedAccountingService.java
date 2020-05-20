package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockedAccountingService implements AccountingService {

    @Override
    public void call(Voucher voucher, VoucherType voucherType, String contractId) {
        log.error("ALERT: Mocked Accounting Service implementation, nothing to do");
    }

}
