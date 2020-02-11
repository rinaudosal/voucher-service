package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherConsumer;
import com.docomodigital.delorean.voucher.service.upload.ConsumeVoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 2020/02/06
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Service
public class VoucherQueueReceiverService {

    private final ConsumeVoucherService consumeVoucherService;

    public VoucherQueueReceiverService(ConsumeVoucherService consumeVoucherService) {
        this.consumeVoucherService = consumeVoucherService;
    }

    public void handleMessage(String message) {
        log.info("Received <" + message + ">");

        // read the message received from WLM
        VoucherConsumer voucherConsumer = consumeVoucherService.readMessage(message);

        // consume an existing voucher
        Voucher voucherConsumed = consumeVoucherService.consumeVoucher(voucherConsumer);

        // send info to WLM
        consumeVoucherService.sendNotification(voucherConsumed);
    }

}
