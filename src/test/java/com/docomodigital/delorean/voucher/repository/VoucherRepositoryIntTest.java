package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.BaseVoucherIntegrationTest;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * 2020/02/03
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherRepositoryIntTest extends BaseVoucherIntegrationTest {

    @Test
    public void findByCodeWorks() {
        VoucherType voucherType = saveVoucherType();
        saveVoucher(voucherType.getId());

        Assertions.assertThat(voucherRepository.existsVoucherByCodeAndTypeIdIn("my_voucher_code", Arrays.asList(voucherType.getId(), "BLABLA"))).isTrue();
        Assertions.assertThat(voucherRepository.existsVoucherByCodeAndTypeIdIn("not_my_voucher_code", Arrays.asList(voucherType.getId(), "BLABLA"))).isFalse();
        Assertions.assertThat(voucherRepository.existsVoucherByCodeAndTypeIdIn("my_voucher_code", Arrays.asList("not_my_voucher_type_code", "BLABLA"))).isFalse();
    }

    private void saveVoucher(String typeId) {
        Voucher voucher = new Voucher();
        voucher.setCode("my_voucher_code");
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTypeId(typeId);

        voucherRepository.save(voucher);
    }

    private VoucherType saveVoucherType() {
        VoucherType voucherType = new VoucherType();
        voucherType.setCode("my_voucher_type_code");
        voucherType.setDescription("my_voucher_type_description");
        voucherType.setAmount(BigDecimal.valueOf(32));
        voucherType.setCurrency("USD");

        voucherType.setProduct("my_product_name");
        voucherType.setPromo("my_promo");
        voucherType.setMerchantId("my_merchant_id");
        voucherType.setPaymentProvider("PAYTM BUUUU");
        voucherType.setCountry("INDIA");
        voucherType.setShopId("my_shop");
        voucherType.setEnabled(true);
        voucherType.setStartDate(LocalDate.of(2020, 1, 1));
        voucherType.setEndDate(LocalDate.of(2020, 11, 8));
        voucherType.setPriority(5);
        voucherType.setBaseUrl("www.test.com");

        return voucherTypeRepository.save(voucherType);
    }
}
