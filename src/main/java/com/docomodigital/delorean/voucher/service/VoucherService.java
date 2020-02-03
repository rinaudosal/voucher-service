package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;

/**
 * * Business class that manage the vouchers
 * 2020/01/29
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherService {

    /**
     * Create a single voucher by input parameters
     *
     * @param code the voucher code to upload
     * @param type the voucher type of the voucher
     * @return the voucher type created
     */
    Vouchers createVoucher(String code, String type);

    /**
     * Upload a file containing list of files
     *
     * @param file file to upload
     * @param type the type for the vouchers
     * @return the stats of the file uploaded
     */
    VoucherUpload uploadVouchers(MultipartFile file, String type);

    /**
     * Purchase a single voucher by code in ACTIVE state
     *
     * @param code            the voucher code to be purchase
     * @param transactionId   the id of the transaction executed to buy the voucher
     * @param transactionDate the date when the transaction are executed
     * @param userId          the user id of the user that are buyed
     * @return the voucher purchased if found
     */
    Vouchers purchaseVoucher(String code, String transactionId, OffsetDateTime transactionDate, String userId);
}
