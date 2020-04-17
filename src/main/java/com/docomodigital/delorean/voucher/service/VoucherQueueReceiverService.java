package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherConsumer;
import com.docomodigital.delorean.voucher.domain.VoucherError;
import com.docomodigital.delorean.voucher.repository.VoucherErrorRepository;
import com.docomodigital.delorean.voucher.service.upload.ConsumeVoucherService;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 2020/02/06
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Service
public class VoucherQueueReceiverService {

    private final ConsumeVoucherService consumeVoucherService;
    private final VoucherErrorRepository voucherErrorRepository;

    public VoucherQueueReceiverService(ConsumeVoucherService consumeVoucherService,
                                       VoucherErrorRepository voucherErrorRepository) {
        this.consumeVoucherService = consumeVoucherService;
        this.voucherErrorRepository = voucherErrorRepository;
    }

    public void handleMessage(byte[] original) {
        String message = new String(original, StandardCharsets.UTF_8);
        log.info("Received <" + message + ">");

        try {
            // read the message received from WLM
            VoucherConsumer voucherToBeConsume = consumeVoucherService.readMessage(message);

            // consume an existing voucher
            Voucher voucherConsumed = consumeVoucherService.consumeVoucher(voucherToBeConsume);

            // send info to WLM by output queue
            consumeVoucherService.sendNotification(voucherConsumed);
        } catch (BadRequestException be) {
            log.error("BadRequest while consuming queue message", be);
            if (!"REQUEST_NOT_BILLED".equals(be.getErrorCode())) {
                VoucherError voucherError = new VoucherError();
                voucherError.setErrorCode(be.getErrorCode());
                voucherError.setErrorMessage(be.getMessage());
                voucherErrorRepository.save(voucherError);
            }
        } catch (Exception e) {
            log.error("Exception while consuming queue message", e);
            VoucherError voucherError = new VoucherError();
            voucherError.setErrorMessage(e.getMessage());
            voucherErrorRepository.save(voucherError);
        }
    }

}
