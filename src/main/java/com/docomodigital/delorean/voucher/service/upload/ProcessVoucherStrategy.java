package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherType;

/**
 * Strategy interface that define the different upload file operations
 * 2019/12/27
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface ProcessVoucherStrategy {

    /**
     * Process to build a voucher from single line and retrieve the collection data
     *
     * @param line     line of file to be process
     * @param type     type of the voucher to be process
     * @param uploadId id of file uploaded
     */
    Voucher processLine(String line, VoucherType type, String uploadId);

    /**
     * Retrieve the voucher type and check the type in base of the operation checks
     *
     * @param type the code of the voucher type
     * @return the VoucherType if found and valid
     */
    VoucherType getValidVoucherType(String type);
}
