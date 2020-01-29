package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.mapper.VoucherMapper;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

/**
 * 2020/01/29
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherMapper voucherMapper;
    private final Clock clock;

    public VoucherServiceImpl(VoucherRepository voucherRepository, VoucherTypeRepository voucherTypeRepository, VoucherMapper voucherMapper, Clock clock) {
        this.voucherRepository = voucherRepository;
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherMapper = voucherMapper;
        this.clock = clock;
    }

    @Override
    public Vouchers createVoucher(String code, String type) {

        VoucherType voucherType = voucherTypeRepository.findByCode(type)
            .orElseThrow(() -> new BadRequestException("NOT_FOUND", "voucherType with code " + type + "not found"));

        // check existing voucher code with the same merchant
        if (voucherRepository.existsVoucherByCodeAndType_MerchantId(code, voucherType.getMerchantId())) {
            throw new BadRequestException("ALREADY_EXIST", "Voucher with code " + code + " already exist");
        }

        if (!voucherType.getEnabled()) {
            throw new BadRequestException("TYPE_DISABLED", "Voucher Type " + type + " is disabled");
        }

        LocalDate today = LocalDate.now(clock);
        if (!voucherType.getEndDate().isAfter(today)) {
            throw new BadRequestException("TYPE_EXPIRED", "Voucher Type " + type + " is expired");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setType(voucherType);

        return voucherMapper.toDto(
            voucherRepository.save(voucher));
    }
}
