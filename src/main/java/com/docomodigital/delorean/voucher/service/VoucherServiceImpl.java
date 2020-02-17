package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.mapper.VoucherMapper;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.service.upload.ProcessVoucherFactory;
import com.docomodigital.delorean.voucher.service.upload.ProcessVoucherStrategy;
import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final VoucherFileService voucherFileService;
    private final VoucherMapper voucherMapper;
    private final Clock clock;
    private static final String ENTITY_NAME = "Voucher Type ";
    private final ProcessVoucherFactory uploadFileFactory;

    public VoucherServiceImpl(VoucherRepository voucherRepository,
                              VoucherTypeRepository voucherTypeRepository,
                              VoucherFileService voucherFileService,
                              VoucherMapper voucherMapper,
                              Clock clock,
                              ProcessVoucherFactory uploadFileFactory
    ) {
        this.voucherRepository = voucherRepository;
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherFileService = voucherFileService;
        this.voucherMapper = voucherMapper;
        this.clock = clock;
        this.uploadFileFactory = uploadFileFactory;
    }

    @Override
    public Vouchers createVoucher(String code, String type) {
        ProcessVoucherStrategy processVoucherStrategy = uploadFileFactory.getUploadFileStrategy(UploadOperation.UPLOAD);

        VoucherType voucherType = processVoucherStrategy.getValidVoucherType(type);

        // check existing voucher code with the same merchant
        List<String> typeIds = voucherTypeRepository.findAllByMerchantId(voucherType.getMerchantId()).stream()
            .map(VoucherType::getId)
            .collect(Collectors.toList());
        if (voucherRepository.existsVoucherByCodeAndTypeIdIn(code, typeIds)) {
            throw new BadRequestException("ALREADY_EXIST", "Voucher with code " + code + " already exist");
        }

        Vouchers vouchers = voucherMapper.toDto(
            voucherRepository.save(
                processVoucherStrategy.processLine(code, voucherType, null)));
        vouchers.setType(type);
        return vouchers;
    }

    @Override
    public VoucherUpload processVouchers(MultipartFile file, String type, UploadOperation uploadOperation) {

        voucherFileService.checkFileToUpload(file);

        ProcessVoucherStrategy processVoucherStrategy = uploadFileFactory.getUploadFileStrategy(uploadOperation);

        VoucherType voucherType = processVoucherStrategy.getValidVoucherType(type);

        return voucherFileService.uploadFile(file, voucherType, uploadOperation, processVoucherStrategy::processLine);
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
            throw new BadRequestException("TYPE_NOT_YET_AVAILABLE", ENTITY_NAME + voucherType.getCode() + " is not yet available");
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

    @Override
    public List<Vouchers> getVouchers(String typeCode, String status, String userId) {
        Voucher voucher = new Voucher();
        voucher.setUserId(StringUtils.trimToNull(userId));

        if (StringUtils.isNotBlank(typeCode)) {
            String typeId = voucherTypeRepository.findByCode(typeCode)
                .map(VoucherType::getId)
                .orElseThrow(() -> new BadRequestException("TYPE_NOT_FOUND", ENTITY_NAME + typeCode + " not found"));
            voucher.setTypeId(StringUtils.trimToNull(typeId));
        }

        if (StringUtils.isNotBlank(status)) {
            if (!EnumUtils.isValidEnum(VoucherStatus.class, status)) {
                throw new BadRequestException("WRONG_STATUS", "Status " + status + " is wrong");
            }
            voucher.setStatus(VoucherStatus.valueOf(status));
        }

        voucher.setCreatedDate(null);
        voucher.setLastModifiedDate(null);
        Example<Voucher> voucherExample = Example.of(voucher);

        return voucherRepository.findAll(voucherExample).stream()
            .map(voucherMapper::toDto)
            .collect(Collectors.toList());
    }
}
