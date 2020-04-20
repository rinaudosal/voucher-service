package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.BaseVoucherIntegrationTest;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 2020/02/03
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherRepositoryIntTest extends BaseVoucherIntegrationTest {

    @Test
    public void findByCodeWorks() {
        VoucherType voucherType = saveVoucherType();
        saveVoucher(voucherType.getId(), VoucherStatus.ACTIVE, "my_voucher_code");

        assertThat(voucherRepository.existsVoucherByCodeAndTypeIdIn("my_voucher_code", Arrays.asList(voucherType.getId(), "BLABLA"))).isTrue();
        assertThat(voucherRepository.existsVoucherByCodeAndTypeIdIn("not_my_voucher_code", Arrays.asList(voucherType.getId(), "BLABLA"))).isFalse();
        assertThat(voucherRepository.existsVoucherByCodeAndTypeIdIn("my_voucher_code", Arrays.asList("not_my_voucher_type_code", "BLABLA"))).isFalse();
    }

    @Test
    public void findByStatusWorks() {
        VoucherType voucherType = saveVoucherType();
        saveVoucher(voucherType.getId(), VoucherStatus.ACTIVE, "my_voucher_code");
        saveVoucher(voucherType.getId(), VoucherStatus.PURCHASED, "my_voucher_code2");
        saveVoucher(voucherType.getId(), VoucherStatus.RESERVED, "my_voucher_code3");
        saveVoucher(voucherType.getId(), VoucherStatus.ACTIVE, "my_voucher_code4");

        checkCollectionByStatus(VoucherStatus.ACTIVE, Arrays.asList("my_voucher_code", "my_voucher_code4"));
        checkCollectionByStatus(VoucherStatus.PURCHASED, Collections.singletonList("my_voucher_code2"));
        checkCollectionByStatus(VoucherStatus.RESERVED, Collections.singletonList("my_voucher_code3"));
    }

    private void checkCollectionByStatus(VoucherStatus voucherStatus, List<String> voucherNames) {
        List<Voucher> voucherList = voucherRepository.findAllByStatus(voucherStatus);
        assertThat(voucherList).hasSize(voucherNames.size());
        List<String> voucherCodes = voucherList.stream().map(Voucher::getCode).collect(Collectors.toList());
        assertThat(voucherCodes).containsExactlyInAnyOrderElementsOf(voucherNames);
    }

    private void saveVoucher(String typeId, VoucherStatus status, String code) {
        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setStatus(status);
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
        voucherType.setStartDate(LocalDateTime.of(2020, 1, 1, 12, 37, 15));
        voucherType.setEndDate(LocalDateTime.of(2020, 11, 8, 12, 37, 15));
        voucherType.setPriority(5);
        voucherType.setBaseUrl("www.test.com");

        return voucherTypeRepository.save(voucherType);
    }
}
