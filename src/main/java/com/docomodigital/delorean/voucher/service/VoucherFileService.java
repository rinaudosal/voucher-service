package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.service.upload.VoucherSingleProcessor;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import org.springframework.web.multipart.MultipartFile;

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
     * @param uploadOperation        the operation candidates
     * @param voucherSingleProcessor processo for single lines
     * @return the Object with statistics data
     */
    VoucherUpload uploadFile(MultipartFile file, VoucherType voucherType, UploadOperation uploadOperation, VoucherSingleProcessor<String, VoucherType, Voucher> voucherSingleProcessor);
}
