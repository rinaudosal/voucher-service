package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.mapper.VoucherTypeMapper;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;
import com.docomodigital.delorean.voucher.web.api.model.VoucherTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
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

    public VoucherTypeServiceImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock, VoucherTypeMapper voucherTypeMapper) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
        this.voucherTypeMapper = voucherTypeMapper;
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
            VoucherType voucherTypeGrouped = types.stream().max(Comparator.comparing(VoucherType::getOrder))
                .orElseThrow(NoSuchElementException::new);

            voucherTypes.add(voucherTypeGrouped);
        });


        return voucherTypes.stream().map(v -> {
            AvailableVoucherTypes availableVoucherTypes = new AvailableVoucherTypes();
            availableVoucherTypes.setCode(v.getCode());
            availableVoucherTypes.setDescription(v.getDescription());
            availableVoucherTypes.setAmount(v.getAmount().getValue());
            availableVoucherTypes.setCurrency(v.getAmount().getCurrency());

            // count the voucher ACTIVE
            Voucher voucher = new Voucher();
            voucher.setStatus(VoucherStatus.ACTIVE);
            voucher.setType(v);
            voucher.setCreatedDate(null);
            voucher.setLastModifiedDate(null);
            availableVoucherTypes.setVoucherAvailable((int) voucherRepository.count(Example.of(voucher)));
            return availableVoucherTypes;
        })
            .filter(v -> v.getVoucherAvailable() > 0)
            .sorted(Comparator.comparing(AvailableVoucherTypes::getCode))//only voucher available
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
        if (StringUtils.isBlank(voucherTypes.getCode())) {
            throw new BadRequestException("MISSING_FIELD", "Invalid voucherTypes, code is mandatory");
        }

        if (voucherTypeRepository.existsVoucherTypeByCode(voucherTypes.getCode())) {
            throw new BadRequestException("ALREADY_EXIST", "Voucher Type already exist");
        }

        if (voucherTypeRepository.existsVoucherTypeByProductAndOrder(voucherTypes.getProduct(), voucherTypes.getOrder())) {
            throw new BadRequestException("SAME_PRODUCT_AND_ORDER", "Voucher Type exist with the same period");
        }

        return voucherTypeMapper.toDto(
            voucherTypeRepository.save(
                voucherTypeMapper.toEntity(voucherTypes)));
    }

    @Override
    public Optional<VoucherTypes> updateVoucherType(String code, VoucherTypes voucherTypes) {
        if (StringUtils.isNotBlank(voucherTypes.getCode())) {
            throw new BadRequestException("WRONG_FIELD", "Cannot pass Voucher Type code in body request");
        }

        return voucherTypeRepository.findByCode(code)
            .map(v -> {
                voucherTypeMapper.updateFromDto(voucherTypes, v);
                voucherTypeRepository.save(v);
                return v;
            })
            .map(voucherTypeMapper::toDto);
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
