package com.docomodigital.delorean.voucher.mapper;


import com.docomodigital.delorean.voucher.BaseUnitTest;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.model.VoucherTypes;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

/**
 * 2020/01/24
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherTypeMapperTest extends BaseUnitTest {
    private VoucherTypeMapper target;

    @Before
    public void setUp() {
        target = new VoucherTypeMapperImpl();
    }

    @Test
    public void singleDtoMappedCorrectly() {
        VoucherType voucherType = getEntity();

        VoucherTypes voucherTypes = target.toDto(voucherType);
        assertDto(voucherTypes);
    }

    @Test
    public void updateFromDtoMappedCorrectly() {
        VoucherTypes voucherTypes = getDto();
        VoucherType voucherType = new VoucherType();
        voucherType.setCode("my_code");
        target.updateFromDto(voucherTypes, voucherType);
        assertEntity(voucherType);
    }

    @Test
    public void nullReferencedUnMappedCorrectly() {
        Assertions.assertThat(target.toDto((VoucherType) null)).isNull();
        Assertions.assertThat(target.toDto((List<VoucherType>) null)).isNull();
        Assertions.assertThat(target.toEntity((VoucherTypes) null)).isNull();
        Assertions.assertThat(target.toEntity((List<VoucherTypes>) null)).isNull();

        final VoucherType voucherType = new VoucherType();
        voucherType.setCountry("bye");
        target.updateFromDto(null, voucherType);
        Assertions.assertThat(voucherType.getCountry()).isEqualTo("bye");

    }

    @Test
    public void listDtoMappedCorrectly() {
        VoucherType voucherType = getEntity();

        List<VoucherTypes> voucherTypes = target.toDto(Collections.singletonList(voucherType));
        Assertions.assertThat(voucherTypes).hasSize(1);
        assertDto(voucherTypes.get(0));
    }

    @Test
    public void singleEntityMappedCorrectly() {
        VoucherTypes voucherTypes = getDto();

        VoucherType voucherType = target.toEntity(voucherTypes);
        assertEntity(voucherType);
    }

    @Test
    public void listEntityMappedCorrectly() {
        VoucherTypes voucherType = getDto();

        List<VoucherType> voucherTypes = target.toEntity(Collections.singletonList(voucherType));
        Assertions.assertThat(voucherTypes).hasSize(1);
        assertEntity(voucherTypes.get(0));
    }

    private VoucherTypes getDto() {
        VoucherTypes voucherType = new VoucherTypes();
        voucherType.setTypeId("my_code");
        voucherType.setProduct("Tinder 1 Month Gold");
        voucherType.setPromo("supersale");
        voucherType.setDescription("my_description");
        voucherType.setCurrency("GHJ");
        voucherType.setAmount(new BigDecimal(42));
        voucherType.setMerchant("my_merchant");
        voucherType.setPaymentProvider("my_payment");
        voucherType.setCountry("my_country");
        voucherType.setShop("my_shop");
        voucherType.setEnabled(true);
        voucherType.setStartDate(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 6, 6, 6), ZoneOffset.UTC));
        voucherType.setEndDate(OffsetDateTime.of(LocalDateTime.of(2020, 1, 2, 6, 6, 6), ZoneOffset.UTC));
        voucherType.setPriority(5);
        return voucherType;
    }

    private VoucherType getEntity() {
        VoucherType voucherType = new VoucherType();
        voucherType.setId("my_id");
        voucherType.setCode("my_code");
        voucherType.setProduct("Tinder 1 Month Gold");
        voucherType.setPromo("supersale");
        voucherType.setDescription("my_description");
        voucherType.setAmount(new BigDecimal(42));
        voucherType.setCurrency("GHJ");

        voucherType.setMerchantId("my_merchant");
        voucherType.setPaymentProvider("my_payment");
        voucherType.setCountry("my_country");
        voucherType.setShopId("my_shop");
        voucherType.setEnabled(true);
        voucherType.setStartDate(LocalDateTime.of(2016, 1, 1, 12, 37, 15));
        voucherType.setEndDate(LocalDateTime.of(2016, 1, 2, 12, 37, 15));
        voucherType.setPriority(5);
        return voucherType;
    }

    private void assertEntity(VoucherType voucherType) {
        Assertions.assertThat(voucherType.getCode()).isEqualTo("my_code");
        Assertions.assertThat(voucherType.getProduct()).isEqualTo("Tinder 1 Month Gold");
        Assertions.assertThat(voucherType.getPromo()).isEqualTo("supersale");
        Assertions.assertThat(voucherType.getDescription()).isEqualTo("my_description");
        Assertions.assertThat(voucherType.getAmount()).isEqualByComparingTo("42");
        Assertions.assertThat(voucherType.getCurrency()).isEqualTo("GHJ");
        Assertions.assertThat(voucherType.getMerchantId()).isEqualTo("my_merchant");
        Assertions.assertThat(voucherType.getCountry()).isEqualTo("my_country");
        Assertions.assertThat(voucherType.getPaymentProvider()).isEqualTo("my_payment");
        Assertions.assertThat(voucherType.getShopId()).isEqualTo("my_shop");
        Assertions.assertThat(voucherType.getEnabled()).isTrue();
        Assertions.assertThat(voucherType.getStartDate()).isEqualTo(LocalDate.of(2016, 1, 1));
        Assertions.assertThat(voucherType.getEndDate()).isEqualTo(LocalDate.of(2016, 1, 2));
        Assertions.assertThat(voucherType.getPriority()).isEqualTo(5);
    }

    private void assertDto(VoucherTypes voucherTypes) {
        Assertions.assertThat(voucherTypes.getTypeId()).isEqualTo("my_code");
        Assertions.assertThat(voucherTypes.getProduct()).isEqualTo("Tinder 1 Month Gold");
        Assertions.assertThat(voucherTypes.getPromo()).isEqualTo("supersale");
        Assertions.assertThat(voucherTypes.getDescription()).isEqualTo("my_description");
        Assertions.assertThat(voucherTypes.getAmount()).isEqualByComparingTo("42");
        Assertions.assertThat(voucherTypes.getCurrency()).isEqualTo("GHJ");
        Assertions.assertThat(voucherTypes.getMerchant()).isEqualTo("my_merchant");
        Assertions.assertThat(voucherTypes.getCountry()).isEqualTo("my_country");
        Assertions.assertThat(voucherTypes.getPaymentProvider()).isEqualTo("my_payment");
        Assertions.assertThat(voucherTypes.getShop()).isEqualTo("my_shop");
        Assertions.assertThat(voucherTypes.getEnabled()).isTrue();
        Assertions.assertThat(voucherTypes.getStartDate()).isEqualTo(LocalDate.of(2016, 1, 1));
        Assertions.assertThat(voucherTypes.getEndDate()).isEqualTo(LocalDate.of(2016, 1, 2));
        Assertions.assertThat(voucherTypes.getPriority()).isEqualTo(5);
    }
}
