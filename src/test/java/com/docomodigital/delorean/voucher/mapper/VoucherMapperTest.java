package com.docomodigital.delorean.voucher.mapper;

import com.docomodigital.delorean.voucher.BaseUnitTest;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

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
public class VoucherMapperTest extends BaseUnitTest {
    private VoucherMapper target;

    @Before
    public void setUp() {
        target = new VoucherMapperImpl();
    }

    @Test
    public void singleDtoMappedCorrectly() {
        Voucher voucher = getEntity();
        Vouchers vouchers = target.toDto(voucher);
        assertDto(vouchers);
    }

    @Test
    public void assertStatusEnum() {
        Assertions.assertThat(((VoucherMapperImpl)target).statusEnumToVoucherStatus(Vouchers.StatusEnum.ACTIVE)).isEqualTo(VoucherStatus.ACTIVE);
        Assertions.assertThat(((VoucherMapperImpl)target).statusEnumToVoucherStatus(Vouchers.StatusEnum.INACTIVE)).isEqualTo(VoucherStatus.INACTIVE);
        Assertions.assertThat(((VoucherMapperImpl)target).statusEnumToVoucherStatus(Vouchers.StatusEnum.PURCHASED)).isEqualTo(VoucherStatus.PURCHASED);
        Assertions.assertThat(((VoucherMapperImpl)target).statusEnumToVoucherStatus(Vouchers.StatusEnum.REDEEMED)).isEqualTo(VoucherStatus.REDEEMED);
        Assertions.assertThat(((VoucherMapperImpl)target).statusEnumToVoucherStatus(null)).isNull();
    }

    @Test
    public void assertVoucherStatus() {
        Assertions.assertThat(((VoucherMapperImpl)target).voucherStatusToStatusEnum(VoucherStatus.ACTIVE)).isEqualTo(Vouchers.StatusEnum.ACTIVE);
        Assertions.assertThat(((VoucherMapperImpl)target).voucherStatusToStatusEnum(VoucherStatus.INACTIVE)).isEqualTo(Vouchers.StatusEnum.INACTIVE);
        Assertions.assertThat(((VoucherMapperImpl)target).voucherStatusToStatusEnum(VoucherStatus.PURCHASED)).isEqualTo(Vouchers.StatusEnum.PURCHASED);
        Assertions.assertThat(((VoucherMapperImpl)target).voucherStatusToStatusEnum(VoucherStatus.REDEEMED)).isEqualTo(Vouchers.StatusEnum.REDEEMED);
        Assertions.assertThat(((VoucherMapperImpl)target).voucherStatusToStatusEnum(null)).isNull();
    }

    @Test
    public void assertNullMapLocalDate() {
        Assertions.assertThat(target.map((LocalDateTime)null)).isNull();
        Assertions.assertThat(target.map((OffsetDateTime) null)).isNull();
    }

    @Test
    public void nullReferencedUnMappedCorrectly() {
        Assertions.assertThat(target.toDto((Voucher) null)).isNull();
        Assertions.assertThat(target.toDto((List<Voucher>) null)).isNull();
        Assertions.assertThat(target.toEntity((Vouchers) null)).isNull();
        Assertions.assertThat(target.toEntity((List<Vouchers>) null)).isNull();
    }

    @Test
    public void listDtoMappedCorrectly() {
        Voucher voucher = getEntity();

        List<Vouchers> vouchers = target.toDto(Collections.singletonList(voucher));
        Assertions.assertThat(vouchers).hasSize(1);
        assertDto(vouchers.get(0));
    }

    @Test
    public void singleEntityMappedCorrectly() {
        Vouchers vouchers = getDto();

        Voucher voucher = target.toEntity(vouchers);
        assertEntity(voucher);
    }

    @Test
    public void listEntityMappedCorrectly() {
        Vouchers voucher = getDto();

        List<Voucher> vouchers = target.toEntity(Collections.singletonList(voucher));
        Assertions.assertThat(vouchers).hasSize(1);
        assertEntity(vouchers.get(0));
    }

    private Vouchers getDto() {
        Vouchers voucher = new Vouchers();
        voucher.setCode("my_code");
        voucher.setTypeId("my_type");
        voucher.setStatus(Vouchers.StatusEnum.ACTIVE);
        voucher.setUserId("my_user_id");
        voucher.setTransactionId("my_transaction_id");
        voucher.setTransactionDate(OffsetDateTime.of(LocalDateTime.of(2020, 2, 2, 22, 22), ZoneOffset.UTC));
        voucher.setPurchaseDate(LocalDate.of(2020, 2, 2));
        voucher.setRedeemDate(LocalDate.of(2020, 2, 2));
        voucher.setActivationUrl("www.test.com");

        return voucher;
    }

    private Voucher getEntity() {
        Voucher voucher = new Voucher();
        voucher.setId("my_id");
        voucher.setCode("my_code");
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTypeId("my_type_id");
        voucher.setUserId("my_user_id");
        voucher.setTransactionId("my_transaction_id");
        voucher.setTransactionDate(LocalDateTime.of(2020, 2, 2, 22, 22));
        voucher.setPurchaseDate(LocalDate.of(2020, 2, 2));
        voucher.setRedeemDate(LocalDate.of(2020, 2, 2));
        voucher.setActivationUrl("www.test.com");
        voucher.setVoucherFileId("my_file_id");

        return voucher;
    }

    private void assertEntity(Voucher voucher) {
        Assertions.assertThat(voucher.getId()).isNull();
        Assertions.assertThat(voucher.getCode()).isEqualTo("my_code");
        Assertions.assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.ACTIVE);
        Assertions.assertThat(voucher.getTypeId()).isEqualTo("my_type");
        Assertions.assertThat(voucher.getUserId()).isEqualTo("my_user_id");
        Assertions.assertThat(voucher.getTransactionId()).isEqualTo("my_transaction_id");
        Assertions.assertThat(voucher.getTransactionDate()).isEqualTo(LocalDateTime.of(2020, 2, 2, 22, 22));
        Assertions.assertThat(voucher.getPurchaseDate()).isEqualTo(LocalDate.of(2020, 2, 2));
        Assertions.assertThat(voucher.getRedeemDate()).isEqualTo(LocalDate.of(2020, 2, 2));
        Assertions.assertThat(voucher.getActivationUrl()).isEqualTo("www.test.com");
        Assertions.assertThat(voucher.getVoucherFileId()).isNull();
    }

    private void assertDto(Vouchers vouchers) {
        Assertions.assertThat(vouchers.getCode()).isEqualTo("my_code");
        Assertions.assertThat(vouchers.getTypeId()).isEqualTo("my_type_id");
        Assertions.assertThat(vouchers.getStatus()).isEqualTo(Vouchers.StatusEnum.ACTIVE);
        Assertions.assertThat(vouchers.getUserId()).isEqualTo("my_user_id");
        Assertions.assertThat(vouchers.getTransactionId()).isEqualTo("my_transaction_id");
        Assertions.assertThat(vouchers.getTransactionDate()).isEqualTo(OffsetDateTime.of(LocalDateTime.of(2020, 2, 2, 22, 22), ZoneOffset.UTC));
        Assertions.assertThat(vouchers.getPurchaseDate()).isEqualTo(LocalDate.of(2020, 2, 2));
        Assertions.assertThat(vouchers.getRedeemDate()).isEqualTo(LocalDate.of(2020, 2, 2));
        Assertions.assertThat(vouchers.getActivationUrl()).isEqualTo("www.test.com");
    }
}
