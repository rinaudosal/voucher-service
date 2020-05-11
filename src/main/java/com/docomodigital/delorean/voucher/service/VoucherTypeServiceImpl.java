package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.client.merchant.model.Shop;
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
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Service
public class VoucherTypeServiceImpl implements VoucherTypeService {

    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherRepository voucherRepository;
    private final Clock clock;
    private final VoucherTypeMapper voucherTypeMapper;
    private final VoucherMapper voucherMapper;
    private final MongoTemplate mongoTemplate;

    public VoucherTypeServiceImpl(VoucherTypeRepository voucherTypeRepository,
                                  VoucherRepository voucherRepository,
                                  Clock clock,
                                  VoucherTypeMapper voucherTypeMapper,
                                  VoucherMapper voucherMapper,
                                  MongoTemplate mongoTemplate) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
        this.voucherTypeMapper = voucherTypeMapper;
        this.voucherMapper = voucherMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<AvailableVoucherTypes> getAvailableVoucherTypes(String merchant, String paymentProvider, String country) {

        // find all voucher type for the merchant, enabled and in correct range date
        Example<VoucherType> voucherTypeExample = getVoucherTypeExample(merchant, paymentProvider, country);

        Map<String, List<VoucherType>> notGrouped = voucherTypeRepository.findAll(voucherTypeExample)
            .stream()
            .filter(vou -> {
                Instant now = Instant.now(clock);
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
    public VoucherType getVoucherType(String shopId, String paymentProvider, String country, String productId) {

        VoucherType voucherType = new VoucherType();
        voucherType.setShopId(shopId);
        voucherType.setPaymentProvider(paymentProvider);
        voucherType.setProduct(productId);
        voucherType.setCountry(country);
        voucherType.setEnabled(true);
        voucherType.setCreatedDate(null);
        voucherType.setLastModifiedDate(null);
        Example<VoucherType> exampleRequest = Example.of(voucherType);

        return voucherTypeRepository.findAll(exampleRequest).stream()
            .filter(vou -> {
                Instant now = Instant.now(clock);
                return now.isBefore(vou.getEndDate()) && now.isAfter(vou.getStartDate()) && this.getVoucherAvailable(vou) > 0;
            })
            .max(Comparator.comparing(VoucherType::getPriority))
            .orElseThrow(() -> new BadRequestException("TYPE_NOT_FOUND",
                String.format("No Voucher Type available for shop %s, paymentProvider %s, country %s and product %s", shopId, paymentProvider, country, productId)));
    }

    @Override
    public Optional<Vouchers> reserveVoucher(String typeId, ReserveRequest reserveRequest) {
        VoucherType type = getValidVoucherType(typeId);

        List<String> voucherTypeIdByMerchantId = voucherTypeRepository.findAllByMerchantId(type.getMerchantId()).stream()
            .map(VoucherType::getId)
            .collect(Collectors.toList());

        if (voucherRepository.existsVoucherByTransactionIdAndTypeIdIn(reserveRequest.getTransactionId(), voucherTypeIdByMerchantId)) {
            throw new BadRequestException(Constants.EXISTING_TRANSACTION_ID_ERROR,
                String.format("Transaction id %s already exist for merchant %s", reserveRequest.getTransactionId(), type.getMerchantId()));
        }

        UpdateResult updateResult = mongoTemplate.updateFirst(
            query(
                where("typeId").is(type.getId())
                    .and("status").is(VoucherStatus.ACTIVE)
            ),
            new Update()
                .set("status", VoucherStatus.RESERVED)
                .set("transactionId", reserveRequest.getTransactionId())
                .set("reserveDate", LocalDateTime.now(clock))
            ,
            Voucher.class
        );

        if (updateResult.getModifiedCount() != 1) {
            throw new BadRequestException(Constants.TRANSACTIONAL_CONFLICT_ERROR, "Errors occurred on update voucher to reserve");
        }

        Voucher voucherUpdated = voucherRepository.findByTypeIdAndTransactionId(type.getId(), reserveRequest.getTransactionId())
            .orElseThrow(() -> new BadRequestException(Constants.VOUCHER_NOT_FOUND_ERROR,
                String.format("Voucher for Type %s and transaction %s not found ", type.getCode(), reserveRequest.getTransactionId())));
        voucherUpdated.setActivationUrl(type.getBaseUrl() + voucherUpdated.getCode());

        log.info(String.format("Reserved voucher %s for the transaction %s", voucherUpdated.getCode(), voucherUpdated.getTransactionId()));
        return Optional.of(voucherRepository.save(voucherUpdated))
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(type.getCode());
                return vouchers;
            });
    }

    @Override
    public VoucherType findById(String id) {
        return voucherTypeRepository.findById(id)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR,
                String.format("Voucher Type %s not found", id)));
    }

    private VoucherType getValidVoucherType(String type) {
        VoucherType voucherType = voucherTypeRepository.findByCode(type)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR,
                String.format("Voucher Type %s not found", type)));

        // User not enabled to reserve
        Shop shop = (Shop) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!shop.getId().equalsIgnoreCase(voucherType.getShopId())) {
            throw new BadRequestException(Constants.UNAUTHORIZED_SHOP_NAME,
                String.format("The shop %s is not enable to reserve vouchers of %s",
                    shop.getId(),
                    voucherType.getShopId()));
        }

        if (!voucherType.getEnabled()) {
            throw new BadRequestException(Constants.TYPE_DISABLED_ERROR, String.format("Voucher Type %s is disabled", type));
        }

        Instant today = Instant.now(clock);
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
