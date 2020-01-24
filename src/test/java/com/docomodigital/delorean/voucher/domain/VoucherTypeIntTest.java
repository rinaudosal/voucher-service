package com.docomodigital.delorean.voucher.domain;

import com.docomodigital.delorean.voucher.BaseVoucherIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Integration test to check if The collection {@link VoucherType} are mapped correctly
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherTypeIntTest extends BaseVoucherIntegrationTest {

    private VoucherType voucherType;

    @Before
    public void setUp() {
        // given i have this voucher that i want to save
        voucherType = new VoucherType();
        voucherType.setCode("my_voucher_type_code");
        voucherType.setDescription("my_voucher_type_description");
        Amount amount = new Amount();
        amount.setValue(BigDecimal.valueOf(32));
        amount.setCurrency("USD");

        voucherType.setAmount(amount);
        voucherType.setMerchantId("my_merchant_id");
        voucherType.setPaymentProvider("PAYTM BUUUU");
        voucherType.setCountry("INDIA");
        voucherType.setShopId("my_shop");
        voucherType.setEnabled(true);
        voucherType.setStartDate(LocalDate.of(2020, 1, 1));
        voucherType.setEndDate(LocalDate.of(2020, 11, 8));

    }

    @Test
    public void amountMandatory() {
        voucherType.setAmount(null);

        // when save the voucher thrown an exception
        Assertions.assertThatThrownBy(() -> voucherTypeRepository.save(voucherType))
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessage("amount: must not be null");
    }

    @Test
    public void merchantIdMandatory() {
        voucherType.setMerchantId(" ");

        // when save the voucher thrown an exception
        Assertions.assertThatThrownBy(() -> voucherTypeRepository.save(voucherType))
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessage("merchantId: must not be blank");
    }

    @Test
    public void shopIdMandatory() {
        voucherType.setShopId(" ");

        // when save the voucher thrown an exception
        Assertions.assertThatThrownBy(() -> voucherTypeRepository.save(voucherType))
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessage("shopId: must not be blank");
    }

    @Test
    public void voucherTypeDataMustBeFilledCorrectly() {
        // when save the voucher
        voucherTypeRepository.save(voucherType);

        // then i save the voucher with technical data filled correctly
        List<VoucherType> voucherTypes = voucherTypeRepository.findAll();
        Assertions.assertThat(voucherTypes).hasSize(1);
        VoucherType returnValue = voucherTypes.get(0);
        Assertions.assertThat(returnValue).isNotNull();

        Assertions.assertThat(returnValue.getId()).isNotNull();
        Assertions.assertThat(returnValue.getCreatedBy()).isNull();
        Assertions.assertThat(returnValue.getCreatedDate()).isNotNull();
        Assertions.assertThat(returnValue.getLastModifiedBy()).isNull();
        Assertions.assertThat(returnValue.getLastModifiedDate()).isNotNull();

        Assertions.assertThat(returnValue.getCode()).isEqualTo("my_voucher_type_code");
        Assertions.assertThat(returnValue.getDescription()).isEqualTo("my_voucher_type_description");
        Assertions.assertThat(returnValue.getAmount().getValue()).isEqualByComparingTo("32");
        Assertions.assertThat(returnValue.getAmount().getCurrency()).isEqualTo("USD");
        Assertions.assertThat(returnValue.getMerchantId()).isEqualTo("my_merchant_id");
        Assertions.assertThat(returnValue.getPaymentProvider()).isEqualTo("PAYTM BUUUU");
        Assertions.assertThat(returnValue.getCountry()).isEqualTo("INDIA");
        Assertions.assertThat(returnValue.getShopId()).isEqualTo("my_shop");
        Assertions.assertThat(returnValue.isEnabled()).isTrue();
        Assertions.assertThat(returnValue.getStartDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        Assertions.assertThat(returnValue.getEndDate()).isEqualTo(LocalDate.of(2020, 11, 8));
    }

}
