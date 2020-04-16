package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 2020/02/05
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class AbstractVoucherStrategyImpl {
    protected final VoucherTypeRepository voucherTypeRepository;
    protected final VoucherRepository voucherRepository;
    protected final Clock clock;

    public AbstractVoucherStrategyImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
    }

    public VoucherType getValidVoucherType(String type) {
        VoucherType voucherType = voucherTypeRepository.findByCode(type)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR,
                String.format("Voucher Type %s not found", type)));

        if (!voucherType.getEnabled()) {
            throw new BadRequestException(Constants.TYPE_DISABLED_ERROR, String.format("Voucher Type %s is disabled", type));
        }


        LocalDateTime today = LocalDateTime.now(clock);
        if (voucherType.getEndDate().isBefore(today)) {

            throw new BadRequestException(Constants.TYPE_EXPIRED_ERROR, String.format("Voucher Type %s is expired", type));
        }

        return voucherType;
    }
}
