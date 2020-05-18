package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRedeem;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRequest;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * * Business class that manage the vouchers
 * 2020/01/29
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherService {

    /**
     * Upload a file containing list of vouchers to upload
     *
     * @param file file to upload
     * @param type the type for the vouchers
     * @return the stats of the file uploaded
     */
    VoucherUpload processVouchers(MultipartFile file, String type);

    /**
     * Upload a file containing list of vouchers to redeem
     *
     * @param file     file to upload
     * @param merchant the merchant identifier
     * @return the stats of the file redeemed
     */

    VoucherRedeem redeemVouchers(MultipartFile file, String merchant);

    /**
     * Retrieve list of vouchers with the filters requested
     *
     * @param typeId        id of the voucher type
     * @param status        status would be find
     * @param userId        user would be find
     * @param merchantId    merchant would be find
     * @param transactionId transaction would be find
     * @return vouchers found
     */
    List<Vouchers> getVouchers(String typeId, String status, String userId, String merchantId, String transactionId);

    Optional<Vouchers> updateVoucher(String code, String typeId, VoucherRequest voucherRequest);

    Optional<Vouchers> getVoucher(String code, String typeId);

    /**
     * Rollback the voucher to ACTIVE state
     *
     * @param voucherExpired the voucher to rollback the reservation
     */
    void restoreToActive(Voucher voucherExpired);

    /**
     * This method retrieve all the vouchers in a specific status
     *
     * @return the List of vouchers RESERVED
     */
    List<Voucher> findAllReservedVouchers();

}
