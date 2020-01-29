package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.web.api.model.Vouchers;

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
}
