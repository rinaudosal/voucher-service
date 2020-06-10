package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.client.merchant.MerchantClient;
import com.docomodigital.delorean.client.merchant.model.Shop;
import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherError;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherErrorRepository;
import lombok.extern.slf4j.Slf4j;
import net.netm.billing.library.AccountingConnection;
import net.netm.billing.library.exception.AccountingException;
import net.netm.billing.library.exception.CDRValidationException;
import net.netm.billing.library.model.CDR;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Slf4j
public class AccountingServiceImpl implements AccountingService {
    public static final int CDR_CLASS = 11;
    public static final int CDR_TYPE = 5;
    public static final int CDR_SERVICE_ID = 16;
    public static final int CDR_INFO_3 = 0;
    public static final int CDR_INFOR_2 = 0;
    public static final int CDR_INFO_1 = 1;
    public static final String CDR_SENDER_ID = "CHARGE";
    public static final BigDecimal CDR_P_FACTOR = new BigDecimal("10000");
    public static final BigDecimal CDR_CHARGING_AMOUNT = new BigDecimal(1);

    private final AccountingConnection accountingConnection;
    private final Clock clock;
    private final VoucherErrorRepository voucherErrorRepository;
    private final MerchantClient merchantClient;

    public AccountingServiceImpl(Clock clock,
                                 VoucherErrorRepository voucherErrorRepository,
                                 AccountingConnection accountingConnection,
                                 MerchantClient merchantClient) {
        this.voucherErrorRepository = voucherErrorRepository;
        this.accountingConnection = accountingConnection;
        this.clock = clock;
        this.merchantClient = merchantClient;
    }

    @Override
    public void call(Voucher voucher, VoucherType voucherType, String contractId) {
        try {
            CDR cdr = createCdr(voucher, voucherType, contractId);
            accountingConnection.chargeOne(cdr);
        } catch (AccountingException e) {
            log.error("Exception while calling CDR", e);
            VoucherError voucherError = new VoucherError();

            voucherError.setErrorCode(Constants.CDR_ACCOUNTING_ERROR);
            voucherError.setErrorMessage(
                String.format("Error on calling CDR with params voucher %s, type %s and contractId %s",
                    voucher.getCode(),
                    voucherType.getCode(),
                    contractId
                ));
            voucherErrorRepository.save(voucherError);
        } catch (CDRValidationException e) {
            log.error("Exception while calling CDR", e);
            VoucherError voucherError = new VoucherError();
            voucherError.setErrorCode(Constants.CDR_VALIDATION_ERROR);
            voucherError.setErrorMessage(
                String.format("Error on calling CDR with params voucher %s, type %s and contractId %s",
                    voucher.getCode(),
                    voucherType.getCode(),
                    contractId
                ));
            voucherErrorRepository.save(voucherError);
        }
    }

    @Override
    public void call(Voucher voucher, VoucherType voucherType) {
        Shop shop = merchantClient.getShopById(voucherType.getShopId());

        this.call(voucher, voucherType, shop.getContractId());
    }

    private CDR createCdr(Voucher voucher, VoucherType voucherType, String contractId) throws CDRValidationException {
        return new CDR.Builder()
            .withContractId(converContract(contractId))
            .withInstanceId(0L)
            .withCdrClass(CDR_CLASS)
            .withCdrType(CDR_TYPE)
            .withOrderEventTimestamp(convertDate(voucher.getTransactionDate()))
            .withServiceEventTimestamp(convertDate(voucher.getPurchaseDate()))
            .withCdrTimestamp(convertDate(clock.instant()))
            .withServiceId(CDR_SERVICE_ID)
            .withTariffClass(1L)
            .withChargingAmount(CDR_CHARGING_AMOUNT)
            .withCostCenter(voucherType.getShopId())
            .withOriginAddress(voucher.getCode())
            .withOriginProtocol(StringUtils.trimToEmpty(voucherType.getPromo()))
            .withOriginId(voucherType.getCode())
            .withSenderId(CDR_SENDER_ID)
            .withDeliveryStatus(1)
            .withPrice(convertAmount(voucherType.getAmount()))
            .withIsPriceGross(false)
            .withCurrency(voucherType.getCurrency())
            .withUniqueMessageId(voucherType.getPaymentProvider() + "_" + voucher.getTransactionId())
            .withSessionId(voucher.getCode())
            .withDestination(voucher.getUserId())
            .withDeliveryElement(voucherType.getPaymentProvider())
            .withMachineId(voucher.getTransactionId())
            .withCdrInfo1(CDR_INFO_1)
            .withCdrInfo2(CDR_INFOR_2)
            .withCdrInfo3(CDR_INFO_3)
            .withCountryId(voucherType.getCountry())
            .withAdditionalInfo("")
            .build();
    }

    private Long converContract(String contractId) throws CDRValidationException {
        if (!NumberUtils.isCreatable(contractId)) {
            throw new CDRValidationException("wrong_contractId");
        }

        return Long.parseLong(contractId);
    }

    private Date convertDate(Instant instant) throws CDRValidationException {
        try {
            return Date.from(instant.atZone(clock.getZone()).toInstant());
        } catch (Exception e) {
            log.debug("Error trying to convert to instant {}", instant);
            throw new CDRValidationException("wrong_date");
        }
    }

    private Integer convertAmount(BigDecimal voucherAmount) throws CDRValidationException {
        if (voucherAmount == null)
            throw new CDRValidationException("wrong_amount");

        return voucherAmount.multiply(CDR_P_FACTOR).intValue();
    }
}
