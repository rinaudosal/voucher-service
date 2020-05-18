package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.client.merchant.model.Shop;
import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.mapper.VoucherMapper;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRedeem;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRequest;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;
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
    private final VoucherTypeService voucherTypeService;
    private final VoucherMapper voucherMapper;
    private final Clock clock;
    private final AccountingService accountingService;

    public VoucherServiceImpl(VoucherRepository voucherRepository,
                              VoucherTypeRepository voucherTypeRepository,
                              VoucherFileService voucherFileService,
                              VoucherTypeService voucherTypeService, VoucherMapper voucherMapper,
                              Clock clock,
                              AccountingService accountingService) {
        this.voucherRepository = voucherRepository;
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherFileService = voucherFileService;
        this.voucherTypeService = voucherTypeService;
        this.voucherMapper = voucherMapper;
        this.clock = clock;
        this.accountingService = accountingService;
    }

    @Override
    public VoucherUpload processVouchers(MultipartFile file, String type) {

        voucherFileService.checkFileToUpload(file);

        VoucherType voucherType = voucherTypeService.getValidVoucherType(type);

        return voucherFileService.uploadFile(file, voucherType);
    }

    @Override
    public VoucherRedeem redeemVouchers(MultipartFile file, String merchant) {
        voucherFileService.checkFileToUpload(file);

        List<VoucherType> voucherTypes = voucherTypeRepository.findAllByMerchantId(merchant);

        return voucherFileService.redeemFile(file, voucherTypes).merchant(merchant);
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

        // User not enabled to reserve
        Shop shop = (Shop) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!shop.getId().equalsIgnoreCase(voucherType.getShopId())) {
            throw new BadRequestException(Constants.UNAUTHORIZED_SHOP_NAME,
                String.format("The shop %s is not enable to reserve vouchers of %s",
                    shop.getId(),
                    voucherType.getShopId()));
        }

        if (!voucherType.getEnabled()) {
            throw new BadRequestException(Constants.TYPE_DISABLED_ERROR, String.format("Voucher Type %s is disabled", typeId));
        }

        Voucher voucher = voucherRepository.findByCodeAndTypeId(code, voucherType.getId())
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format("Voucher %s not found for type %s", code, typeId)));

        if (!VoucherStatus.RESERVED.equals(voucher.getStatus())) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR, String.format("Reservation for the voucher code %s has expired or voucher it is already purchased", code));
        }

        if (!voucherRequest.getTransactionId().equalsIgnoreCase(voucher.getTransactionId())) {
            throw new BadRequestException(Constants.WRONG_TRANSACTION_ID_ERROR,
                String.format("Transaction id %s is different of reserved %s",
                    voucherRequest.getTransactionId(),
                    voucher.getTransactionId()));
        }

        if (VoucherRequest.TransactionStatusEnum.SUCCESS.equals(voucherRequest.getTransactionStatus())) {
            voucher.setTransactionId(voucherRequest.getTransactionId());
            voucher.setTransactionDate(voucherRequest.getTransactionDate().toInstant());
            voucher.setStatus(VoucherStatus.PURCHASED);
            voucher.setPurchaseDate(Instant.now(clock));
            voucher.setAmount(voucherRequest.getAmount());
            voucher.setCurrency(voucherRequest.getCurrency());
            voucher.setUserId(voucherRequest.getUserId());
            if (!Strings.isNullOrEmpty(shop.getContractId())) {
                accountingService.call(voucher, voucherType, shop.getContractId());
            }
        } else {
            resetToActive(voucher);
        }

        return Optional.of(voucherRepository.save(voucher))
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(typeId);

                return vouchers;
            });
    }

    @Override
    public Optional<Vouchers> getVoucher(String code, String typeId) {
        VoucherType voucherType = voucherTypeRepository.findByCode(typeId)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, typeId)));

        // User not enabled to reserve
        Shop shop = (Shop) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!shop.getId().equalsIgnoreCase(voucherType.getShopId())) {
            throw new BadRequestException(Constants.UNAUTHORIZED_SHOP_NAME,
                String.format("The shop %s is not enable to get vouchers of %s",
                    shop.getId(),
                    voucherType.getShopId()));
        }

        Voucher voucher = voucherRepository.findByCodeAndTypeId(code, voucherType.getId())
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format("Voucher %s not found for type %s", code, typeId)));

        if (!(VoucherStatus.PURCHASED.equals(voucher.getStatus()) ||
            VoucherStatus.RESERVED.equals(voucher.getStatus()) ||
            VoucherStatus.REDEEMED.equals(voucher.getStatus()))) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR, String.format("Voucher with code %s is not Billed", code));
        }

        return Optional.of(voucher)
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(typeId);

                return vouchers;
            });
    }

    @Override
    public void restoreToActive(Voucher voucherExpired) {
        resetToActive(voucherExpired);
        voucherRepository.save(voucherExpired);
    }

    @Override
    public List<Voucher> findAllReservedVouchers() {
        return voucherRepository.findAllByStatus(VoucherStatus.RESERVED);
    }

    private void resetToActive(Voucher voucher) {
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
}
