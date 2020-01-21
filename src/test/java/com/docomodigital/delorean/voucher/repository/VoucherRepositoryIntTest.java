package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.BaseVoucherIntegrationTest;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherRepositoryIntTest extends BaseVoucherIntegrationTest {

    @Before
    public void setUp() {
    }

    @Test
    public void auditDataMustBeFilledCorrectly() {
        // given i have this voucher that i want to save
        VoucherType voucherType = new VoucherType();
        voucherType.setType(VoucherType.TYPE.M3);
        voucherType.setDescription("voucher type 3 months");
        voucherType.setMerchantCode("tinder");

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
    }

}
