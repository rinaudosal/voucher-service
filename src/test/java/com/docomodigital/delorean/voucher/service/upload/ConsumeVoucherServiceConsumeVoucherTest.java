package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.BaseUnitTest;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherConsumer;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.service.VoucherTypeService;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 2020/02/10
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class ConsumeVoucherServiceConsumeVoucherTest extends BaseUnitTest {
    private ConsumeVoucherService target;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private VoucherTypeService voucherTypeService;

    @Captor
    private ArgumentCaptor<Voucher> voucherArgumentCaptor;
    private VoucherConsumer input;

    @Before
    public void setUp() {
        target = new ConsumeVoucherServiceImpl(voucherTypeService, voucherRepository, clock);

        Instant instant = LocalDate.parse("17/12/2002", DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay().toInstant(ZoneOffset.UTC);
        setupClockMock(instant);

        VoucherType voucherType = new VoucherType();
        voucherType.setId("my_type_id");
        voucherType.setBaseUrl("www.google.com/");
        BDDMockito.given(voucherTypeService.getVoucherType(
            Mockito.eq("my_merchant_id"),
            Mockito.eq("my_payment_provider"),
            Mockito.eq("my_country"),
            Mockito.eq("my_product_id"))).willReturn(voucherType);

        Voucher voucher = new Voucher();
        voucher.setCode("PIPPO");
        BDDMockito.given(voucherRepository.findFirstByTypeIdAndStatusEquals(
            Mockito.eq("my_type_id"),
            Mockito.eq(VoucherStatus.ACTIVE))).willReturn(Optional.of(voucher));

        Mockito.when(voucherRepository.save(Mockito.any(Voucher.class)))
            .thenAnswer((Answer) invocation -> invocation.getArguments()[0]);

        input = new VoucherConsumer();
        input.setMerchantId("my_merchant_id");
        input.setPaymentProvider("my_payment_provider");
        input.setCountry("my_country");
        input.setProductId("my_product_id");
        input.setUserId("my_user_id");
        input.setTransactionId("my_transaction_id");
        input.setTransactionDate(LocalDateTime.of(2020, 2, 2, 2, 2, 2));
        input.setBillingStatus("BILLED");
    }

    @Test
    public void voucherConsumedCorrectly() {

        Voucher voucherReturned = target.consumeVoucher(input);

        Mockito.verify(voucherRepository, Mockito.times(1))
            .save(voucherArgumentCaptor.capture());

        Voucher voucherSaved = voucherArgumentCaptor.getValue();
        checkVoucher(voucherReturned);
        checkVoucher(voucherSaved);
    }

    @Test
    public void voucherTypeNotFound() {
        BDDMockito.given(voucherTypeService.getVoucherType(
            Mockito.eq("my_merchant_id"),
            Mockito.eq("my_payment_provider"),
            Mockito.eq("my_country"),
            Mockito.eq("my_product_id"))).willReturn(null);

        Assertions.assertThatThrownBy(() -> target.consumeVoucher(input))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("errorCode", "TYPE_NOT_FOUND")
            .hasMessage("Voucher type not found");

    }

    @Test
    public void voucherNotFound() {

        BDDMockito.given(voucherRepository.findFirstByTypeIdAndStatusEquals(
            Mockito.eq("my_type_id"),
            Mockito.eq(VoucherStatus.ACTIVE))).willReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> target.consumeVoucher(input))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("errorCode", "VOUCHER_NOT_FOUND")
            .hasMessage("Voucher with type my_type_id and status ACTIVE not found");

    }

    @Test
    public void requestNotBilled() {

        input.setBillingStatus("NOOOO");

        Assertions.assertThatThrownBy(() -> target.consumeVoucher(input))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("errorCode", "REQUEST_NOT_BILLED")
            .hasMessage("Wrong type of request, status must be BILLED, request status is NOOOO");

    }


    // status not billed

    private void checkVoucher(Voucher voucher) {
        Assertions.assertThat(voucher).isNotNull();
        Assertions.assertThat(voucher.getCode()).isEqualTo("PIPPO");
        Assertions.assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.PURCHASED);
        Assertions.assertThat(voucher.getActivationUrl()).isEqualTo("www.google.com/PIPPO");
        Assertions.assertThat(voucher.getPurchaseDate()).isEqualTo(LocalDate.of(2002, 12, 17));
    }
}
