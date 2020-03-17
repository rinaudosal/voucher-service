package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.config.Constants;
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
import com.docomodigital.delorean.voucher.web.api.model.VoucherRequest;
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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
            throw new BadRequestException(Constants.ALREADY_EXIST_ERROR, String.format("Voucher with code %s already exist", code));
        }

        Vouchers vouchers = voucherMapper.toDto(
            voucherRepository.save(processVoucherStrategy.processLine(code, voucherType, null)));
        vouchers.setTypeId(type);
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
            .orElseThrow(() -> new BadRequestException(Constants.VOUCHER_NOT_FOUND_ERROR, String.format("Voucher %s not found", code)));

        if (!VoucherStatus.ACTIVE.equals(voucher.getStatus())) {
            throw new BadRequestException(Constants.VOUCHER_NOT_ACTIVE_ERROR, String.format("Voucher with code %s is not in ACTIVE state", code));
        }

        VoucherType voucherType = voucherTypeRepository.findById(voucher.getTypeId())
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, voucher.getTypeId())));

        if (LocalDate.now(clock).isBefore(voucherType.getStartDate())) {
            throw new BadRequestException(Constants.TYPE_NOT_YET_AVAILABLE_ERROR, String.format("Voucher Type %s is not yet available", voucherType.getCode()));
        }

        if (!voucherType.getEnabled()) {
            throw new BadRequestException(Constants.TYPE_DISABLED_ERROR, String.format("Voucher Type %s is disabled", voucherType.getCode()));
        }

        voucher.setActivationUrl(voucherType.getBaseUrl() + voucher.getCode());
        voucher.setStatus(VoucherStatus.PURCHASED);
        voucher.setUserId(userId);
        voucher.setTransactionId(transactionId);
        voucher.setTransactionDate(transactionDate.toLocalDateTime());
        voucher.setPurchaseDate(LocalDateTime.now(clock));


        return voucherMapper.toDto(voucherRepository.save(voucher));
    }

    @Override
    public List<Vouchers> getVouchers(String typeCode, String status, String userId, String merchantId, String transactionId) {
        Voucher voucher = new Voucher();
        voucher.setUserId(StringUtils.trimToNull(userId));
        voucher.setTransactionId(StringUtils.trimToNull(transactionId));
        if (StringUtils.isNotBlank(typeCode)) {

            VoucherType type = voucherTypeRepository.findByCode(typeCode)
                .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, typeCode)));

            if (StringUtils.isBlank(merchantId) || type.getMerchantId().equals(merchantId)) {
                voucher.setTypeId(StringUtils.trimToNull(type.getId()));
            } else {
                throw new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, typeCode));
            }
        }

        if (StringUtils.isNotBlank(status)) {
            if (!EnumUtils.isValidEnum(VoucherStatus.class, status)) {
                throw new BadRequestException(Constants.WRONG_STATUS_ERROR, String.format("Status %s is wrong", status));
            }
            voucher.setStatus(VoucherStatus.valueOf(status));
        }

        voucher.setCreatedDate(null);
        voucher.setLastModifiedDate(null);
        Example<Voucher> voucherExample = Example.of(voucher);

        return voucherRepository.findAll(voucherExample).stream()
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(voucherTypeRepository.findById(v.getTypeId()).map(VoucherType::getCode).orElse(null));
                return vouchers;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Vouchers> updateVoucher(String code, String typeId, VoucherRequest voucherRequest) {
        VoucherType voucherType = voucherTypeRepository.findByCode(typeId)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, typeId)));

        if (!voucherType.getEnabled()) {
            throw new BadRequestException(Constants.TYPE_DISABLED_ERROR, String.format("Voucher Type %s is disabled", typeId));
        }

        Voucher voucher = voucherRepository.findByCode(code)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format("Voucher %s not found for type %s", code, typeId)));

        if (!VoucherStatus.RESERVED.equals(voucher.getStatus())) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR, String.format("Voucher with code %s is not in RESERVED state", code));
        }

        if(!voucherRequest.getTransactionId().equalsIgnoreCase(voucher.getTransactionId())) {
            throw new BadRequestException(Constants.WRONG_TRANSACTION_ID_ERROR,
                String.format("Transaction id %s is different of reserved %s",
                    voucherRequest.getTransactionId(),
                    voucher.getTransactionId()));
        }

        if (VoucherRequest.TransactionStatusEnum.SUCCESS.equals(voucherRequest.getTransactionStatus())) {
            voucher.setTransactionId(voucherRequest.getTransactionId());
            voucher.setTransactionDate(voucherRequest.getTransactionDate().toLocalDateTime());
            voucher.setStatus(VoucherStatus.PURCHASED);
            voucher.setPurchaseDate(LocalDateTime.now(clock));
            voucher.setAmount(voucherRequest.getAmount());
            voucher.setCurrency(voucherRequest.getCurrency());
            voucher.setUserId(voucherRequest.getUserId());
        } else {
            voucher.setTransactionId(null);
            voucher.setTransactionDate(null);
            voucher.setAmount(null);
            voucher.setCurrency(null);
            voucher.setStatus(VoucherStatus.ACTIVE);
            voucher.setPurchaseDate(null);
            voucher.setReserveDate(null);
            voucher.setUserId(null);
            voucher.setActivationUrl(null);
        }

        return Optional.of(voucherRepository.save(voucher))
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(typeId);

                return vouchers;
            });
    }
}
