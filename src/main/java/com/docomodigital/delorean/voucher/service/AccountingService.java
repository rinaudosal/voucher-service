package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.domain.resource.Shop;
import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import net.netm.billing.library.AccountingConnection;
import net.netm.billing.library.exception.AccountingException;
import net.netm.billing.library.exception.CDRValidationException;
import net.netm.billing.library.model.CDR;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CDRService {
    private final AccountingConnection accsrv;

    public CDRService() {

    }

    public void call(Voucher voucher, VoucherType voucherType, Shop shop) {
        try {
            CDR cdr = createCdr(voucher, voucherType, shop.getContractId());
            if (cdr != null) {
                accsrv.chargeOne(cdr);
            }
        } catch (AccountingException e) {
            e.printStackTrace();
        }
    }

}
