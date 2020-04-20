package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.BaseVoucherIntegrationTest;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 2020/02/03
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherTypeRepositoryIntTest extends BaseVoucherIntegrationTest {

    @Test
    public void findByCodeWorks() {
        saveVoucherType("my_voucher_type_code", "my_merchant_id");

        Assertions.assertThat(voucherTypeRepository.findByCode("my_voucher_type_code")).isPresent();
        Assertions.assertThat(voucherTypeRepository.findByCode("NOTHING")).isNotPresent();
    }

    @Test
    public void existsVoucherTypeByCodeWorks() {
        saveVoucherType("my_voucher_type_code", "my_merchant_id");

        Assertions.assertThat(voucherTypeRepository.existsVoucherTypeByCode("my_voucher_type_code")).isTrue();
        Assertions.assertThat(voucherTypeRepository.existsVoucherTypeByCode("NOTHING")).isFalse();
    }

    @Test
    public void findAllIdByMerchantIdWorks() {
        saveVoucherType("my_voucher_type_code", "my_merchant_id");
        saveVoucherType("my_voucher_type_code2", "my_merchant_id");
        saveVoucherType("my_voucher_type_code3", "not_my_merchant_id");

        List<VoucherType> merchants = voucherTypeRepository.findAllByMerchantId("my_merchant_id");
        Assertions.assertThat(merchants).hasSize(2);
    }

    private void saveVoucherType(String code, String merchantId) {
        VoucherType voucherType = new VoucherType();
        voucherType.setCode(code);
        voucherType.setDescription("my_voucher_type_description");
        voucherType.setAmount(BigDecimal.valueOf(32));
        voucherType.setCurrency("USD");

        voucherType.setProduct("my_product_name");
        voucherType.setPromo("my_promo");
        voucherType.setMerchantId(merchantId);
        voucherType.setPaymentProvider("PAYTM BUUUU");
        voucherType.setCountry("INDIA");
        voucherType.setShopId("my_shop");
        voucherType.setEnabled(true);
        voucherType.setStartDate(LocalDateTime.of(2020, 1, 1, 12, 37, 15).toInstant(ZoneOffset.UTC));
        voucherType.setEndDate(LocalDateTime.of(2020, 11, 8, 12, 37, 15).toInstant(ZoneOffset.UTC));
        voucherType.setPriority(5);
        voucherType.setBaseUrl("www.test.com");

        voucherTypeRepository.save(voucherType);
    }
}
