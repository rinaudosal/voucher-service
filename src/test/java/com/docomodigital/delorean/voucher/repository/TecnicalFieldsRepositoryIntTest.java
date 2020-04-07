package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.BaseVoucherIntegrationTest;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

/**
 * 2020/02/03
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class TecnicalFieldsRepositoryIntTest extends BaseVoucherIntegrationTest {

    private Voucher voucher;

    @Before
    public void setUp() {
        voucher = saveVoucher();
    }

    @Test
    public void technicalDateOnInsertWorksCorrectly() {
        Assertions.assertThat(voucher.getCreatedDate()).isPresent();
        Assertions.assertThat(voucher.getLastModifiedDate()).isPresent();
        Instant createInstant = voucher.getCreatedDate().get();
        Instant modifiedInstant = voucher.getLastModifiedDate().get();
        Assertions.assertThat(createInstant).isNotNull();
        Assertions.assertThat(modifiedInstant).isNotNull();
        Assertions.assertThat(createInstant).isEqualTo(modifiedInstant);
    }

    @Test
    public void technicalDateOnUpdateWorksCorrectly() {
        Assertions.assertThat(voucher.getCreatedDate()).isPresent();
        Instant createInstant = voucher.getCreatedDate().get();

        voucher.setRedeemFileId("vgdr");
        voucher = voucherRepository.save(voucher);

        Assertions.assertThat(voucher.getCreatedDate()).isPresent();
        Assertions.assertThat(voucher.getCreatedDate().get()).isEqualTo(createInstant);

        Assertions.assertThat(voucher.getLastModifiedDate()).isPresent();
        Assertions.assertThat(voucher.getLastModifiedDate().get()).isNotEqualTo(createInstant);
    }

    private Voucher saveVoucher() {
        Voucher voucher = new Voucher();
        voucher.setCode("my_voucher_code");
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTypeId("bfcd");

        return voucherRepository.save(voucher);
    }

}
