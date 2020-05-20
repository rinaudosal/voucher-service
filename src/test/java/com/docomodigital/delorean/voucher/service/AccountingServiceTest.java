package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.BaseUnitTest;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherError;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherErrorRepository;
import net.netm.billing.library.AccountingConnection;
import net.netm.billing.library.exception.AccountingException;
import net.netm.billing.library.exception.CDRValidationException;
import net.netm.billing.library.model.CDR;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 2020/05/14
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class AccountingServiceTest extends BaseUnitTest {
    private AccountingService target;

    @Mock
    private VoucherErrorRepository voucherErrorRepository;

    @Mock
    private AccountingConnection accountingConnection;

    @Captor
    private ArgumentCaptor<VoucherError> voucherErrorArgumentCaptor;

    @Captor
    private ArgumentCaptor<CDR> cdrArgumentCaptor;
    private Voucher voucher;
    private VoucherType voucherType;

    @Before
    public void setUp() {
        setupClockMock(Instant.ofEpochSecond(21));
        target = new AccountingServiceImpl(clock, voucherErrorRepository, accountingConnection);
        voucher = new Voucher();
        voucher.setTransactionDate(Instant.ofEpochMilli(123456));
        voucher.setPurchaseDate(Instant.ofEpochMilli(654321));
        voucher.setCode("my_code");
        voucher.setUserId("my_user");
        voucher.setTransactionId("my-txt");

        voucherType = new VoucherType();
        voucherType.setShopId("my_shop");
        voucherType.setPromo("my_promo");
        voucherType.setAmount(BigDecimal.ONE);
        voucherType.setCurrency("EUR");
        voucherType.setPaymentProvider("my-pp");
        voucherType.setCountry("MY");
        voucherType.setCode("my_type");
    }

    @Test
    public void callCDRCorrectlyWithAllValues() throws Exception {
        target.call(voucher, voucherType, "123456");

        Mockito.verify(voucherErrorRepository, Mockito.never())
            .save(Mockito.any(VoucherError.class));

        Mockito.verify(accountingConnection).chargeOne(cdrArgumentCaptor.capture());

        CDR cdr = cdrArgumentCaptor.getValue();

        assertCDR(cdr);
    }

    @Test
    public void saveAnErrorWhenChargeOneThrowsAccountingException() throws Exception {
        Mockito
            .doAnswer(invocation -> {
                throw new AccountingException("pippo");
            })
            .when(accountingConnection).chargeOne(Mockito.any(CDR.class));

        target.call(voucher, voucherType, "123456");
        checkValidationError("CDR_ACCOUNTING_ERROR", "123456");

    }

    private void checkValidationError(String cdr_accounting_error, String contractId) {
        Mockito.verify(voucherErrorRepository).save(voucherErrorArgumentCaptor.capture());

        VoucherError voucherError = voucherErrorArgumentCaptor.getValue();
        assertThat(voucherError).isNotNull();
        assertThat(voucherError.getErrorCode()).isEqualTo(cdr_accounting_error);
        assertThat(voucherError.getErrorMessage()).isEqualTo("Error on calling CDR with params voucher my_code, type my_type and contractId " + contractId);
    }

    @Test
    public void saveAnErrorWhenPassNullInstant() {
        voucher.setTransactionDate(null);

        target.call(voucher, voucherType, "123456");
        checkValidationError("CDR_VALIDATION_ERROR", "123456");
    }

    @Test
    public void saveAnErrorWhenPassNullAmount() {
        voucherType.setAmount(null);

        target.call(voucher, voucherType, "123456");
        checkValidationError("CDR_VALIDATION_ERROR", "123456");
    }

    @Test
    public void saveAnErrorWhenPassWrongContractId() {
        target.call(voucher, voucherType, "123456s");
        checkValidationError("CDR_VALIDATION_ERROR", "123456s");
    }

    @Test
    public void saveAnErrorWhenChargeOneThrowsCdrException() throws Exception {

        Mockito
            .doAnswer(invocation -> {
                throw new CDRValidationException("pippo");
            })
            .when(accountingConnection).chargeOne(Mockito.any(CDR.class));

        target.call(voucher, voucherType, "123456");

        checkValidationError("CDR_VALIDATION_ERROR", "123456");
    }

    private void assertCDR(CDR cdr) {
        assertThat(cdr.getContractId()).isEqualTo(123456L);
        assertThat(cdr.getInstanceId()).isEqualTo(0L);
        assertThat(cdr.getCdrClass().getCdrClassNumber()).isEqualTo(11);
        assertThat(cdr.getCdrType().getCdrTypeNumber()).isEqualTo(5);
        assertThat(cdr.getOrderEventTimestamp().toInstant()).isEqualTo(Instant.ofEpochMilli(123456));
        assertThat(cdr.getServiceEventTimestamp().toInstant()).isEqualTo(Instant.ofEpochMilli(654321));
        assertThat(cdr.getCdrTimestamp().toInstant()).isEqualTo(clock.instant());
        assertThat(cdr.getServiceId()).isEqualTo(16);
        assertThat(cdr.getTariffClass()).isEqualTo(1L);
        assertThat(cdr.getChargingAmount()).isOne();
        assertThat(cdr.getCostCenter()).isEqualTo("my_shop");
        assertThat(cdr.getOriginAddress()).isEqualTo("my_code");
        assertThat(cdr.getOriginProtocol()).isEqualTo("my_promo");
        assertThat(cdr.getOriginId()).isEqualTo("my_type");
        assertThat(cdr.getSenderId()).isEqualTo("CHARGE");
        assertThat(cdr.getDeliveryStatus().getDeliveryStatusNumber()).isEqualTo(1);
        assertThat(cdr.getPrice()).isEqualTo(10000);
        assertThat(cdr.getIsPriceGross()).isFalse();
        assertThat(cdr.getCurrency()).isEqualTo("EUR");
        assertThat(cdr.getUniqueMessageId()).isEqualTo("my-pp_my-txt");

        assertThat(cdr.getSessionId()).isEqualTo("my_code");
        assertThat(cdr.getDestination()).isEqualTo("my_user");
        assertThat(cdr.getDeliveryElement()).isEqualTo("my-pp");
        assertThat(cdr.getMachineId()).isEqualTo("my-txt");
        assertThat(cdr.getCountryId()).isEqualTo("MY");

        assertThat(cdr.getCdrInfo1().getCdrInfo1Number()).isEqualTo(1);
        assertThat(cdr.getCdrInfo2().getCdrInfo2Number()).isEqualTo(0);
        assertThat(cdr.getCdrInfo3().getCdrInfo3Number()).isEqualTo(0);
        assertThat(cdr.getAdditionalInfo()).isEqualTo("");
        assertThat(cdr.getVersion()).isEqualTo("20150624");
    }

}
