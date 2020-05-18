package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Component
public class RedeemVoucherComponent {
    private final VoucherRepository voucherRepository;

    public RedeemVoucherComponent(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public Voucher processLine(String line, List<VoucherType> voucherTypes, String uploadId) {
        String[] voucherData = StringUtils.split(line, ",");
        if (voucherData.length != 3) {
            throw new BadRequestException("INVALID_DATA", "Invalid Data line");
        }

        String code = voucherData[1];
        Instant dateRedeemed = LocalDateTime.parse(voucherData[2], DateTimeFormatter.ofPattern("M/d/yy H:mm")).toInstant(ZoneOffset.UTC);

        Voucher voucher = voucherRepository.findByCodeAndTypeIdIn(code, voucherTypes.stream().map(VoucherType::getId).collect(Collectors.toList()))
            .orElseThrow(() -> new BadRequestException(Constants.VOUCHER_NOT_FOUND_ERROR,
                String.format("Voucher %s not found", code)));

        VoucherType voucherType = voucherTypes.stream().filter(t -> t.getId().equals(voucher.getTypeId())).findFirst()
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format("Voucher type %s not found", voucher.getTypeId())));

        if (!VoucherStatus.PURCHASED.equals(voucher.getStatus()) && BooleanUtils.isNotTrue(voucherType.getBypassStatusCheck())) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR,
                String.format("Voucher %s not redeemed, the status is %s", code, voucher.getStatus()));
        }

        voucher.setStatus(VoucherStatus.REDEEMED);
        voucher.setRedeemDate(dateRedeemed);
        voucher.setRedeemFileId(uploadId);

        return voucher;
    }

}
