package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * 2019/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Component
public class ProcessVoucherFactory {

    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherRepository voucherRepository;
    private final Clock clock;

    public ProcessVoucherFactory(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
    }

    public ProcessVoucherStrategy getUploadFileStrategy(UploadOperation uploadOperation) {
        switch (uploadOperation) {
            case UPLOAD:
                return new UploadVoucherStrategyImpl(voucherTypeRepository, voucherRepository, clock);
            case REDEEM:
                return new RedeemVoucherStrategyImpl(voucherTypeRepository, voucherRepository, clock);
            case PURCHASE:
                return new PurchaseVoucherStrategyImpl(voucherTypeRepository, voucherRepository, clock);
            default:
                throw new UnsupportedOperationException("Operation non implemented for " + uploadOperation);
        }
    }


}
