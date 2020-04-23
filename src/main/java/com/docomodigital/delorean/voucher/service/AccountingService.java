package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherError;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import lombok.extern.slf4j.Slf4j;
import net.netm.billing.library.AccountingConnection;
import net.netm.billing.library.exception.AccountingException;
import net.netm.billing.library.exception.CDRValidationException;
import net.netm.billing.library.model.CDR;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class AccountingService {
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

    public AccountingService(Clock clock) {
        this.accountingConnection = new AccountingConnection();
        this.clock = clock;
    }

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
        }
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
            .withOriginProtocol(voucherType.getPromo())
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
            //.withDistanceTable(null) //FIXME fill it or remove
            .build();
    }

    private Long converContract(String contractId) {
        return NumberUtils.isCreatable(contractId) ? Long.parseLong(contractId) : 0L;
    }

    private Date convertDate(Instant instant) {
        Date result = null;
        try {
            result = Date.from(instant.atZone(clock.getZone()).toInstant());
        } catch (Exception e) {
            log.debug("Error trying to convert to instant {}", instant);
        }
        return result;
    }

    private Integer convertAmount(BigDecimal voucherAmount) {
        BigDecimal result = voucherAmount.multiply(CDR_P_FACTOR);
        return result.intValue();
    }
}
