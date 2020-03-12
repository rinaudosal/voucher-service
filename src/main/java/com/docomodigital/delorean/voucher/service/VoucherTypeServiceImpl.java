package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.mapper.VoucherMapper;
import com.docomodigital.delorean.voucher.mapper.VoucherTypeMapper;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;
import com.docomodigital.delorean.voucher.web.api.model.ReserveRequest;
import com.docomodigital.delorean.voucher.web.api.model.VoucherTypes;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Service
public class VoucherTypeServiceImpl implements VoucherTypeService {

    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherRepository voucherRepository;
    private final Clock clock;
    private final VoucherTypeMapper voucherTypeMapper;
    private final VoucherMapper voucherMapper;

    public VoucherTypeServiceImpl(VoucherTypeRepository voucherTypeRepository,
                                  VoucherRepository voucherRepository,
                                  Clock clock,
                                  VoucherTypeMapper voucherTypeMapper,
                                  VoucherMapper voucherMapper) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
        this.voucherTypeMapper = voucherTypeMapper;
        this.voucherMapper = voucherMapper;
    }

    @Override
    public List<AvailableVoucherTypes> getAvailableVoucherTypes(String merchant, String paymentProvider, String country) {

        // find all voucher type for the merchant, enabled and in correct range date
        Example<VoucherType> voucherTypeExample = getVoucherTypeExample(merchant, paymentProvider, country);

        Map<String, List<VoucherType>> notGrouped = voucherTypeRepository.findAll(voucherTypeExample)
            .stream()
            .filter(vou -> {
                LocalDate now = LocalDate.now(clock);
                return now.isBefore(vou.getEndDate()) && now.isAfter(vou.getStartDate());
            }).collect(Collectors.groupingBy(VoucherType::getProduct));

        List<VoucherType> voucherTypes = new ArrayList<>();
        notGrouped.forEach((product, types) -> {
            VoucherType voucherTypeGrouped = types.stream().max(Comparator.comparing(VoucherType::getPriority))
                .orElseThrow(NoSuchElementException::new);

            voucherTypes.add(voucherTypeGrouped);
        });

        return voucherTypes.stream().map(v -> {
            AvailableVoucherTypes availableVoucherTypes = new AvailableVoucherTypes();
            availableVoucherTypes.setTypeId(v.getCode());
            availableVoucherTypes.setDescription(v.getDescription());
            availableVoucherTypes.setAmount(v.getAmount());
            availableVoucherTypes.setCurrency(v.getCurrency());
            availableVoucherTypes.setVoucherAvailable(this.getVoucherAvailable(v));
            return availableVoucherTypes;
        })
            .filter(v -> v.getVoucherAvailable() > 0)
            .sorted(Comparator.comparing(AvailableVoucherTypes::getTypeId))
            .collect(Collectors.toList());
    }

    @Override
    public List<VoucherTypes> getVoucherTypes(Example<VoucherType> example) {
        List<VoucherType> domainList = voucherTypeRepository.findAll(example);
        return voucherTypeMapper.toDto(domainList);
    }

    @Override
    public Optional<VoucherTypes> getVoucherType(String code) {
        return voucherTypeRepository.findByCode(code)
            .map(voucherTypeMapper::toDto);
    }

    @Override
    public VoucherTypes createVoucherType(VoucherTypes voucherTypes) {
        if (StringUtils.isBlank(voucherTypes.getTypeId())) {
            throw new BadRequestException("MISSING_FIELD", "Invalid voucherTypes, typeId is mandatory");
        }

        if (voucherTypeRepository.existsVoucherTypeByCode(voucherTypes.getTypeId())) {
            throw new BadRequestException("ALREADY_EXIST", "Voucher Type already exist");
        }

        if (voucherTypeRepository.existsVoucherTypeByProductAndPriority(voucherTypes.getProduct(), voucherTypes.getPriority())) {
            throw new BadRequestException("SAME_PRODUCT_AND_ORDER", "Voucher Type exist with the same period");
        }

        return voucherTypeMapper.toDto(
            voucherTypeRepository.save(
                voucherTypeMapper.toEntity(voucherTypes)));
    }

    @Override
    public Optional<VoucherTypes> updateVoucherType(String code, VoucherTypes voucherTypes) {
        if (StringUtils.isNotBlank(voucherTypes.getTypeId())) {
            throw new BadRequestException("WRONG_FIELD", "Cannot pass Voucher Type typeId in body request");
        }

        return voucherTypeRepository.findByCode(code)
            .map(v -> {
                voucherTypeMapper.updateFromDto(voucherTypes, v);
                voucherTypeRepository.save(v);
                return v;
            })
            .map(voucherTypeMapper::toDto);
    }

    @Override
    public VoucherType getVoucherType(String merchantId, String paymentProvider, String country, String productId) {

        VoucherType voucherType = new VoucherType();
        voucherType.setMerchantId(merchantId);
        voucherType.setPaymentProvider(paymentProvider);
        voucherType.setProduct(productId);
        voucherType.setCountry(country);
        voucherType.setEnabled(true);
        voucherType.setCreatedDate(null);
        voucherType.setLastModifiedDate(null);
        Example<VoucherType> exampleRequest = Example.of(voucherType);

        return voucherTypeRepository.findAll(exampleRequest).stream()
            .filter(vou -> {
                LocalDate now = LocalDate.now(clock);
                return now.isBefore(vou.getEndDate()) && now.isAfter(vou.getStartDate()) && this.getVoucherAvailable(vou) > 0;
            })
            .max(Comparator.comparing(VoucherType::getPriority))
            .orElseThrow(() -> new BadRequestException("TYPE_NOT_FOUND",
                String.format("No Voucher Type available for merchant %s, paymentProvider %s, country %s and product %s", merchantId, paymentProvider, country, productId)));
    }

    @Override
    public Optional<Vouchers> reserveVoucher(String typeId, ReserveRequest reserveRequest) {
        VoucherType type = getValidVoucherType(typeId);

        Voucher voucherToBeReserve = voucherRepository.findFirstByTypeIdAndStatusEquals(type.getId(), VoucherStatus.ACTIVE)
            .orElseThrow(() -> new BadRequestException(Constants.VOUCHER_NOT_FOUND_ERROR,
                String.format("Voucher with type %s and status ACTIVE not found", typeId)));

        voucherToBeReserve.setStatus(VoucherStatus.RESERVED);
        voucherToBeReserve.setTransactionId(reserveRequest.getTransactionId());
        voucherToBeReserve.setReserveDate(LocalDateTime.now(clock));
        voucherToBeReserve.setActivationUrl(type.getBaseUrl() + voucherToBeReserve.getCode());

        return Optional.of(voucherRepository.save(voucherToBeReserve))
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(type.getCode());
                return vouchers;
            });
    }


    private VoucherType getValidVoucherType(String type) {
        VoucherType voucherType = voucherTypeRepository.findByCode(type)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR,
                String.format("Voucher Type %s not found", type)));

        if (!voucherType.getEnabled()) {
            throw new BadRequestException(Constants.TYPE_DISABLED_ERROR, String.format("Voucher Type %s is disabled", type));
        }

        LocalDate today = LocalDate.now(clock);
        if (!voucherType.getEndDate().isAfter(today)) {
            throw new BadRequestException(Constants.TYPE_EXPIRED_ERROR, String.format("Voucher Type %s is expired", type));
        }

        if (voucherType.getStartDate().isAfter(today)) {
            throw new BadRequestException(Constants.TYPE_NOT_YET_AVAILABLE_ERROR, String.format("Voucher Type %s is not yet available", type));
        }

        return voucherType;
    }

    private int getVoucherAvailable(VoucherType v) {
        Voucher voucher = new Voucher();
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTypeId(v.getId());
        voucher.setCreatedDate(null);
        voucher.setLastModifiedDate(null);
        return (int) voucherRepository.count(Example.of(voucher));
    }

    private Example<VoucherType> getVoucherTypeExample(String merchant, String paymentProvider, String country) {
        VoucherType voucherType = new VoucherType();
        voucherType.setMerchantId(merchant);
        voucherType.setPaymentProvider(paymentProvider);
        voucherType.setCountry(country);
        voucherType.setEnabled(true);
        voucherType.setCreatedDate(null);
        voucherType.setLastModifiedDate(null);

        // retrieve one type for each product, convert to dto and collect
        return Example.of(voucherType);
    }
}
