package com.docomodigital.delorean.voucher.job;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.service.VoucherService;
import com.docomodigital.delorean.voucher.service.VoucherTypeService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2020/04/16
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Component
public class VoucherScheduledJobs {

    private final Clock clock;
    private final VoucherService voucherService;
    private final VoucherTypeService voucherTypeService;

    public VoucherScheduledJobs(Clock clock,
                                VoucherService voucherService,
                                VoucherTypeService voucherTypeService) {
        this.clock = clock;
        this.voucherService = voucherService;
        this.voucherTypeService = voucherTypeService;
    }

    /**
     * Scheduled Job to rollback the voucher reserved
     */
    @Timed
    @Scheduled(fixedRateString = "${job.reservationExpired.fixedRate-in-milliseconds}")
    public void reservationExpired() {
        log.info("Starting reservationExpired() method to rollback vouchers..");
        Map<String, List<Voucher>> vouchersReserved = voucherService.findAllReservedVouchers()
            .stream()
            .collect(Collectors.groupingBy(Voucher::getTypeId))            ;

        vouchersReserved
            .forEach((type, vouchers) -> {
                VoucherType voucherType = voucherTypeService.findById(type);
                log.info(String.format("There are %d voucher RESERVED for voucher type %s, check if expired..",
                    vouchers.size(), voucherType.getCode()));

                vouchers.stream()
                    .filter(voucher -> {
                        if (voucherType.getExpiryTimeReservation() != null && voucherType.getExpiryTimeReservation() > 0) {
                            long currentTimeInMillis = clock.instant().toEpochMilli();
                            long expireDateInMillis = voucher.getReserveDate().atZone(clock.getZone()).toInstant().toEpochMilli()
                                + voucherType.getExpiryTimeReservation();

                            return currentTimeInMillis >= expireDateInMillis;
                        }

                        return false;
                    })
                    .forEach(voucherExpired -> {
                        log.info(String.format("Voucher %s was reserved at %s and is expired, restore to ACTIVE..",
                            voucherExpired.getCode(), voucherExpired.getReserveDate()));

                        voucherService.restoreToActive(voucherExpired);
                    });
            });

    }

}
