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
public class RedeemVoucherStrategyImpl implements ProcessVoucherStrategy {
    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherRepository voucherRepository;
    private final Clock clock;

    public RedeemVoucherStrategyImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
    }

    @Override
    public Voucher processLine(String line, VoucherType type, String uploadId) {
        Voucher voucher = voucherRepository.findByCodeAndTypeId(line, type.getId())
            .orElseThrow(() -> new BadRequestException(Constants.VOUCHER_NOT_FOUND_ERROR,
                String.format("Voucher %s not found for Type %s", line, type.getCode())));
        if (!VoucherStatus.PURCHASED.equals(voucher.getStatus())) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR,
                String.format("Voucher %s not redeemed, the status is %s", line, voucher.getStatus()));
        }

        voucher.setStatus(VoucherStatus.REDEEMED);
        voucher.setRedeemDate(LocalDate.now(clock));
        voucher.setVoucherFileId(uploadId);

        return voucher;
    }

    @Override
    public VoucherType getValidVoucherType(String type) {
        return voucherTypeRepository.findByCode(type)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR,
                String.format("Voucher Type %s not found", type)));
    }

}
