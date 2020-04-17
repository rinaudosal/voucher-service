package com.docomodigital.delorean.voucher.job;

import com.docomodigital.delorean.voucher.BaseUnitTest;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.service.VoucherService;
import com.docomodigital.delorean.voucher.service.VoucherTypeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 2020/04/16
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherScheduledJobsTest extends BaseUnitTest {
    private VoucherScheduledJobs target;


    @Mock
    private VoucherService voucherService;

    @Mock
    private VoucherTypeService voucherTypeService;

    @Before
    public void setUp() {
        target = new VoucherScheduledJobs(clock, voucherService, voucherTypeService);
    }

    @Test
    public void nothingToDoIfQueryReturnsEmptyValues() {
        BDDMockito.given(voucherService.findAllReservedVouchers())
            .willReturn(Collections.emptyList());

        target.reservationExpired();

        BDDMockito.then(voucherService)
            .should()
            .findAllReservedVouchers();

        BDDMockito.then(voucherTypeService)
            .should(Mockito.never())
            .findById(Mockito.anyString());

        BDDMockito.then(voucherService)
            .should(Mockito.never())
            .restoreToActive(Mockito.any(Voucher.class));
    }

    @Test
    public void nothingToDoIfVoucherTypeHaventExpireDateReturnsEmptyValues() {
        Instant instant = LocalDateTime
            .parse("2011-12-03T10:15:30.200", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .toInstant(ZoneOffset.UTC);
        setupClockMock(instant);

        List<Voucher> voucherList = new ArrayList<>();
        Voucher voucher1 = new Voucher();
        voucher1.setCode("example_code");
        voucher1.setTypeId("type_id");
        voucher1.setReserveDate(LocalDateTime.parse("2011-12-03T10:15:30.190", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        voucherList.add(voucher1);

        Voucher voucher = new Voucher();
        voucher.setCode("example_code2");
        voucher.setTypeId("type_id_2");
        voucher.setReserveDate(LocalDateTime.parse("2011-12-03T10:15:30.191", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        voucherList.add(voucher);
        BDDMockito.given(voucherService.findAllReservedVouchers())
            .willReturn(voucherList);


        VoucherType voucherType = new VoucherType();
        BDDMockito.given(voucherTypeService.findById(Mockito.eq("type_id")))
            .willReturn(voucherType);
        BDDMockito.given(voucherTypeService.findById(Mockito.eq("type_id_2")))
            .willReturn(voucherType);

        target.reservationExpired();

        BDDMockito.then(voucherService)
            .should()
            .findAllReservedVouchers();

        BDDMockito.then(voucherTypeService)
            .should()
            .findById("type_id");

        BDDMockito.then(voucherTypeService)
            .should()
            .findById("type_id_2");


        BDDMockito.then(voucherService)
            .should(Mockito.never())
            .restoreToActive(Mockito.any(Voucher.class));
    }


    @Test
    public void shouldCallVoucherTypeIfVouchersFound() {
        Instant instant = LocalDateTime
            .parse("2011-12-03T10:15:30.200", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .toInstant(ZoneOffset.UTC);
        setupClockMock(instant);

        List<Voucher> voucherList = new ArrayList<>();

        Voucher voucher1 = new Voucher();
        voucher1.setCode("example_code");
        voucher1.setTypeId("type_id");
        voucher1.setReserveDate(LocalDateTime.parse("2011-12-03T10:15:30.190", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        voucherList.add(voucher1);

        Voucher voucher = new Voucher();
        voucher.setCode("example_code2");
        voucher.setTypeId("type_id_2");
        voucher.setReserveDate(LocalDateTime.parse("2011-12-03T10:15:30.191", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        voucherList.add(voucher);

        BDDMockito.given(voucherService.findAllReservedVouchers())
            .willReturn(voucherList);

        VoucherType voucherType = new VoucherType();
        voucherType.setExpiryTimeReservation(10);

        BDDMockito.given(voucherTypeService.findById(Mockito.eq("type_id")))
            .willReturn(voucherType);
        BDDMockito.given(voucherTypeService.findById(Mockito.eq("type_id_2")))
            .willReturn(voucherType);

        target.reservationExpired();

        BDDMockito.then(voucherService)
            .should()
            .findAllReservedVouchers();

        BDDMockito.then(voucherTypeService)
            .should()
            .findById("type_id");

        BDDMockito.then(voucherTypeService)
            .should()
            .findById("type_id_2");

        BDDMockito.then(voucherService)
            .should()
            .restoreToActive(Mockito.eq(voucher1));

    }


}
