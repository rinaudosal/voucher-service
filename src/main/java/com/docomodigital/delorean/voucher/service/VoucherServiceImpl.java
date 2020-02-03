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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        List<String> typeIds = voucherTypeRepository.findAllByMerchantId(voucherType.getMerchantId()).stream()
            .map(VoucherType::getId)
            .collect(Collectors.toList());
        if (voucherRepository.existsVoucherByCodeAndTypeIdIn(code, typeIds)) {
            throw new BadRequestException("ALREADY_EXIST", "Voucher with code " + code + " already exist");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTypeId(voucherType.getId());

        Vouchers vouchers = voucherMapper.toDto(voucherRepository.save(voucher));
        vouchers.setType(type);
        return vouchers;
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

    @Override
    public Vouchers purchaseVoucher(String code, String transactionId, OffsetDateTime transactionDate, String userId) {

        Voucher voucher = voucherRepository.findByCode(code)
            .orElseThrow(() -> new BadRequestException("VOUCHER_NOT_FOUND", "Voucher " + code + " not found"));
        if (!VoucherStatus.ACTIVE.equals(voucher.getStatus())) {
            throw new BadRequestException("VOUCHER_NOT_ACTIVE", "Voucher with code " + code + " is not in ACTIVE state");
        }

        VoucherType voucherType = voucherTypeRepository.findById(voucher.getTypeId())
            .orElseThrow(() -> new BadRequestException("TYPE_NOT_FOUND", ENTITY_NAME + voucher.getTypeId() + " not found"));

        if (LocalDate.now(clock).isBefore(voucherType.getStartDate())) {
            throw new BadRequestException("TYPE_NOT_YET_AVAILABLE", "Voucher Type " + voucherType.getCode() + " is not yet available");
        }

        if (!voucherType.getEnabled()) {
            throw new BadRequestException("TYPE_DISABLED", ENTITY_NAME + voucherType.getCode() + " is disabled");
        }

        voucher.setActivationUrl(voucherType.getBaseUrl() + voucher.getCode());


        voucher.setStatus(VoucherStatus.PURCHASED);
        voucher.setUserId(userId);
        voucher.setTransactionId(transactionId);
        voucher.setTransactionDate(transactionDate.toLocalDateTime());
        voucher.setPurchaseDate(LocalDate.now(clock));


        return voucherMapper.toDto(voucherRepository.save(voucher));
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
