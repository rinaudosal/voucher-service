package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Component
public class PurchaseVoucherStrategyImpl extends AbstractVoucherStrategyImpl implements ProcessVoucherStrategy {

    public PurchaseVoucherStrategyImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock) {
        super(voucherTypeRepository, voucherRepository, clock);
    }

    @Override
    public Voucher processLine(String line, VoucherType type, String uploadId) {
        Voucher voucher = voucherRepository.findByCodeAndTypeId(line, type.getId())
            .orElseThrow(() -> new BadRequestException(Constants.VOUCHER_NOT_FOUND_ERROR,
                String.format("Voucher %s not found for Type %s", line, type.getCode())));

        if (!VoucherStatus.ACTIVE.equals(voucher.getStatus())) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR,
                String.format("Voucher with code %s is not in ACTIVE state", line));
        }

        voucher.setStatus(VoucherStatus.PURCHASED);
        voucher.setPurchaseDate(LocalDate.now(clock));
        voucher.setVoucherFileId(uploadId);

        return voucher;
    }

}
