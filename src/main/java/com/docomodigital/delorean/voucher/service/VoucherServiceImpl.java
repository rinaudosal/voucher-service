package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.mapper.VoucherMapper;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;

/**
 * 2020/01/29
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherFileComponent voucherFileComponent;
    private final VoucherMapper voucherMapper;
    private final Clock clock;
    private static final String ENTITY_NAME = "Voucher Type ";

    public VoucherServiceImpl(VoucherRepository voucherRepository,
                              VoucherTypeRepository voucherTypeRepository,
                              VoucherFileComponent voucherFileComponent,
                              VoucherMapper voucherMapper,
                              Clock clock) {
        this.voucherRepository = voucherRepository;
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherFileComponent = voucherFileComponent;
        this.voucherMapper = voucherMapper;
        this.clock = clock;
    }

    @Override
    public Vouchers createVoucher(String code, String type) {

        VoucherType voucherType = getValidVoucherType(type);

        // check existing voucher code with the same merchant
        if (voucherRepository.existsVoucherByCodeAndType_MerchantId(code, voucherType.getMerchantId())) {
            throw new BadRequestException("ALREADY_EXIST", "Voucher with code " + code + " already exist");
        }


        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setType(voucherType);

        return voucherMapper.toDto(
            voucherRepository.save(voucher));
    }

    @Override
    public VoucherUpload uploadVouchers(MultipartFile file, String type) {

        voucherFileComponent.checkFileToUpload(file);

        VoucherType voucherType = getValidVoucherType(type);

        try {
            return voucherFileComponent.uploadFile(file, voucherType);
        } catch (IOException e) {
            log.error("IOException on upload file", e);
        }

        return null;
    }

    private VoucherType getValidVoucherType(String type) {
        VoucherType voucherType = voucherTypeRepository.findByCode(type)
            .orElseThrow(() -> new BadRequestException("TYPE_NOT_FOUND", ENTITY_NAME + type + " not found"));


        if (!voucherType.getEnabled()) {
            throw new BadRequestException("TYPE_DISABLED", ENTITY_NAME + type + " is disabled");
        }

        LocalDate today = LocalDate.now(clock);
        if (!voucherType.getEndDate().isAfter(today)) {
            throw new BadRequestException("TYPE_EXPIRED", ENTITY_NAME + type + " is expired");
        }

        return voucherType;
    }
}
