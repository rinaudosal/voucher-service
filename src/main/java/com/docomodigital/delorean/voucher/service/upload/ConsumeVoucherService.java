package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherConsumer;

/**
 * 2020/02/07
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface ConsumeVoucherService {

    /**
     * Method to read input string from queue and retrieve an object if valid
     *
     * @param message data input from queue
     * @return the voucher data to process consuming of input
     */
    VoucherConsumer readMessage(String message);

    /**
     * Receive an object with the information to consume a single voucher from family of voucher type
     *
     * @param voucherConsumer info to consume the voucher
     * @return voucher consumed
     */
    Voucher consumeVoucher(VoucherConsumer voucherConsumer);

    /**
     * Method to send notification on requestor
     *
     * @param voucher the voucher billed
     */
    void sendNotification(Voucher voucher) throws Exception; //NOSONAR

}
