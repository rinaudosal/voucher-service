package com.docomodigital.delorean.voucher.domain;

import com.docomodigital.delorean.voucher.BaseVoucherIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Integration test to check
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherIntTest extends BaseVoucherIntegrationTest {

    private Voucher voucher;

    @Before
    public void setUp() {
        // given i have this voucher that i want to save
        voucher = new Voucher();
        voucher.setCode("my_voucher_code");
        voucher.setStatus(VoucherStatus.PURCHASED);

        VoucherType type = new VoucherType();
        type.setId("my_id");
        type.setCode("dcds");
        type.setAmount(BigDecimal.TEN);
        type.setCurrency("cd");
        type.setProduct("vdsv");
        type.setPaymentProvider("bfcdbfd");
        type.setMerchantId("vdsv");
        type.setCountry("fdvfd");
        type.setShopId("dvsd");
        type.setPriority(3);
        voucherTypeRepository.save(type);

        voucher.setTypeId(type.getId());

    }


    @Test
    public void minCodeConstraint() {
        voucher.setCode("123");
        // when save the voucher thrown an exception
        Assertions.assertThatThrownBy(() -> voucherRepository.save(voucher))
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessage("code: size must be between 6 and 60");
    }

    @Test
    public void maxCodeConstraint() {
        voucher.setCode("123121321568484545s64ds56c456ds4c5ds4c54ds56c4ds5c4sd56c4ds564cds565464");
        // when save the voucher thrown an exception
        Assertions.assertThatThrownBy(() -> voucherRepository.save(voucher))
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessage("code: size must be between 6 and 60");
    }

    @Test
    public void statusConstraint() {
        voucher.setStatus(null);

        // when save the voucher thrown an exception
        Assertions.assertThatThrownBy(() -> voucherRepository.save(voucher))
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessage("status: must not be null");
    }

    @Test
    public void typeConstraint() {
        voucher.setTypeId(null);

        // when save the voucher thrown an exception
        Assertions.assertThatThrownBy(() -> voucherRepository.save(voucher))
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessage("typeId: must not be null");
    }

    @Test
    public void voucherDataMustBeFilledCorrectlyWithAllData() {
        voucher.setId("vvdvfdv");
        voucher.setUserId("my_user_id");
        voucher.setTransactionId("my_trans_id");
        voucher.setTransactionDate(LocalDateTime.now());
        voucher.setPurchaseDate(LocalDateTime.now());
        voucher.setRedeemDate(LocalDateTime.now());
        voucher.setActivationUrl("www.test.com");
        voucher.setCreatedBy("me");
        voucher.setCreatedDate(Instant.now());
        voucher.setLastModifiedBy("you");
        voucher.setLastModifiedDate(Instant.now());
        voucher.setVoucherFileId("123d546d");

        // when save the voucher
        voucherRepository.save(voucher);

        // then i save the voucher no voucher type must be saved
        List<VoucherType> voucherTypes = voucherTypeRepository.findAll();
        Assertions.assertThat(voucherTypes).hasSize(1);

        List<Voucher> vouchers = voucherRepository.findAll();
        Assertions.assertThat(vouchers).hasSize(1);
        Voucher returnValue = vouchers.get(0);
        Assertions.assertThat(returnValue).isNotNull();

        Assertions.assertThat(returnValue.getCode()).isEqualTo("my_voucher_code");
        Assertions.assertThat(returnValue.getStatus()).isEqualTo(VoucherStatus.PURCHASED);
        Assertions.assertThat(returnValue.getTypeId()).isNotNull();
        Assertions.assertThat(returnValue.getUserId()).isEqualTo("my_user_id");
        Assertions.assertThat(returnValue.getTransactionId()).isEqualTo("my_trans_id");
        Assertions.assertThat(returnValue.getTransactionDate()).isNotNull();
        Assertions.assertThat(returnValue.getPurchaseDate()).isNotNull();
        Assertions.assertThat(returnValue.getRedeemDate()).isNotNull();
        Assertions.assertThat(returnValue.getActivationUrl()).isNotNull();

        Assertions.assertThat(returnValue.getId()).isNotNull();
        Assertions.assertThat(returnValue.getCreatedBy()).isNotNull();
        Assertions.assertThat(returnValue.getCreatedDate()).isNotNull();
        Assertions.assertThat(returnValue.getLastModifiedBy()).isNotNull();
        Assertions.assertThat(returnValue.getLastModifiedDate()).isNotNull();
        Assertions.assertThat(returnValue.getVoucherFileId()).isEqualTo("123d546d");

    }

    @Test
    public void voucherDataMustBeFilledCorrectly() {
        // when save the voucher
        voucherRepository.save(voucher);

        // then i save the voucher no voucher type must be saved
        List<VoucherType> voucherTypes = voucherTypeRepository.findAll();
        Assertions.assertThat(voucherTypes).hasSize(1);

        List<Voucher> vouchers = voucherRepository.findAll();
        Assertions.assertThat(vouchers).hasSize(1);
        Voucher returnValue = vouchers.get(0);
        Assertions.assertThat(returnValue).isNotNull();

        Assertions.assertThat(returnValue.getCode()).isEqualTo("my_voucher_code");
        Assertions.assertThat(returnValue.getStatus()).isEqualTo(VoucherStatus.PURCHASED);
        Assertions.assertThat(returnValue.getTypeId()).isNotNull();
        Assertions.assertThat(returnValue.getUserId()).isNull();
        Assertions.assertThat(returnValue.getTransactionId()).isNull();
        Assertions.assertThat(returnValue.getTransactionDate()).isNull();
        Assertions.assertThat(returnValue.getPurchaseDate()).isNull();
        Assertions.assertThat(returnValue.getRedeemDate()).isNull();
        Assertions.assertThat(returnValue.getActivationUrl()).isNull();
        Assertions.assertThat(returnValue.getVoucherFileId()).isNull();

        Assertions.assertThat(returnValue.getId()).isNotNull();
        Assertions.assertThat(returnValue.getCreatedBy()).isNull();
        Assertions.assertThat(returnValue.getCreatedDate()).isNotNull();
        Assertions.assertThat(returnValue.getLastModifiedBy()).isNull();
        Assertions.assertThat(returnValue.getLastModifiedDate()).isNotNull();
    }

}
