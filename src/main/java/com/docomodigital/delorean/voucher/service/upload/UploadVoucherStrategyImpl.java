package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Component
public class UploadVoucherStrategyImpl implements ProcessVoucherStrategy {

    private static final String VOUCHER_TYPE_ENTITY_NAME = "Voucher Type ";
    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherRepository voucherRepository;
    private final Clock clock;

    public UploadVoucherStrategyImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
    }

    @Override
    public Voucher processLine(String line, VoucherType type, String uploadId) {
        if(voucherRepository.existsVoucherByCodeAndTypeIdIn(line, Collections.singletonList(type.getId()))) {
            throw new BadRequestException("ALREADY_EXIST", "Voucher " + line + " already exist");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(line);
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTypeId(type.getId());
        voucher.setVoucherFileId(uploadId);

        return voucher;
    }

    @Override
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
