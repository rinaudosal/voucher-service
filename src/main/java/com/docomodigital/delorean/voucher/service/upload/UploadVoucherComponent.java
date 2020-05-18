package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Component
public class UploadVoucherComponent {
    private final VoucherRepository voucherRepository;

    public UploadVoucherComponent(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public Voucher processLine(String line, List<VoucherType> types, String uploadId) {
        if (voucherRepository.existsVoucherByCodeAndTypeIdIn(line, Collections.singletonList(types.get(0).getId()))) {
            throw new BadRequestException("ALREADY_EXIST", "Voucher " + line + " already exist");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(line);
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTypeId(types.get(0).getId());
        voucher.setVoucherFileId(uploadId);

        return voucher;
    }

}
