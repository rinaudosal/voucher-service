package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRedeem;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 2020/02/04
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherFileService {

    /**
     * Check the file that want to upload
     *
     * @param file the multipart file
     */
    void checkFileToUpload(MultipartFile file);

    /**
     * Upload file after checks
     *
     * @param file                   the file to upload
     * @param voucherType            the type of the vouchers to be process
     * @return the Object with statistics data
     */
    VoucherUpload uploadFile(MultipartFile file, VoucherType voucherType);

    VoucherRedeem redeemFile(MultipartFile file, List<VoucherType> voucherTypes);
}
