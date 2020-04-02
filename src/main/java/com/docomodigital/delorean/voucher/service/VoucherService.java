package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRequest;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
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
     * Upload a file containing list of files
     *
     * @param file            file to upload
     * @param type            the type for the vouchers
     * @param uploadOperation the operation to upload
     * @return the stats of the file uploaded
     */
    VoucherUpload processVouchers(MultipartFile file, String type, UploadOperation uploadOperation);


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
}
