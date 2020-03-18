package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Collections;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Component
public class UploadVoucherStrategyImpl extends AbstractVoucherStrategyImpl implements ProcessVoucherStrategy {
    public UploadVoucherStrategyImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock) {
        super(voucherTypeRepository, voucherRepository, clock);
    }

    @Override
    public Voucher processLine(String line, VoucherType type, String uploadId) {
        if (voucherRepository.existsVoucherByCodeAndTypeIdIn(line, Collections.singletonList(type.getId()))) {
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
    public boolean skipHeaderLine() {
        return false;
    }
}
