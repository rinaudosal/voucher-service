package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Component
public class RedeemVoucherStrategyImpl implements ProcessVoucherStrategy {
    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherRepository voucherRepository;

    public RedeemVoucherStrategyImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
    }

    @Override
    public Voucher processLine(String line, VoucherType type, String uploadId) {
        String[] voucherData = StringUtils.split(line, ",");

        if (voucherData.length != 3) {
            throw new BadRequestException("INVALID_DATA", "Invalid Data line");
        }

        String code = voucherData[1];
        LocalDateTime dateRedeemed = LocalDateTime.parse(voucherData[2], DateTimeFormatter.ofPattern("d/M/yy H:mm"));

        Voucher voucher = voucherRepository.findByCodeAndTypeId(code, type.getId())
            .orElseThrow(() -> new BadRequestException(Constants.VOUCHER_NOT_FOUND_ERROR,
                String.format("Voucher %s not found for Type %s", code, type.getCode())));
        if (!VoucherStatus.PURCHASED.equals(voucher.getStatus())) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR,
                String.format("Voucher %s not redeemed, the status is %s", code, voucher.getStatus()));
        }

        voucher.setStatus(VoucherStatus.REDEEMED);
        voucher.setRedeemDate(dateRedeemed);
        voucher.setRedeemFileId(uploadId);

        return voucher;
    }

    @Override
    public VoucherType getValidVoucherType(String type) {
        return voucherTypeRepository.findByCode(type)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR,
                String.format("Voucher Type %s not found", type)));
    }

    @Override
    public boolean skipHeaderLine() {
        return true;
    }

}
