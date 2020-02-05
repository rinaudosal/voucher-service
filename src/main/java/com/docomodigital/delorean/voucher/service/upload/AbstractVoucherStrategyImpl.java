package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;

import java.time.Clock;
import java.time.LocalDate;

/**
 * 2020/02/05
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class AbstractVoucherStrategyImpl {
    private static final String VOUCHER_TYPE_ENTITY_NAME = "Voucher Type ";
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
            .orElseThrow(() -> new BadRequestException("TYPE_NOT_FOUND", VOUCHER_TYPE_ENTITY_NAME + type + " not found"));

        if (!voucherType.getEnabled()) {
            throw new BadRequestException("TYPE_DISABLED", VOUCHER_TYPE_ENTITY_NAME + type + " is disabled");
        }

        LocalDate today = LocalDate.now(clock);
        if (!voucherType.getEndDate().isAfter(today)) {
            throw new BadRequestException("TYPE_EXPIRED", VOUCHER_TYPE_ENTITY_NAME + type + " is expired");
        }

        return voucherType;
    }
}
