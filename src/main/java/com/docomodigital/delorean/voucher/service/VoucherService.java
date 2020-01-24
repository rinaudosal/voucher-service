package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;

import java.util.List;

/**
 * Business class that manage the voucher types
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherService {


    /**
     * Method that retrieve the available products
     *
     * @param merchant        the merchant owner of the vouchers
     * @param paymentProvider the payment provider that distribute the vouchers
     * @param country         the country where the voucher are distributed
     * @return the List of {@link AvailableVoucherTypes} if founds, empty otherwise
     */
    List<AvailableVoucherTypes> getAvailableVoucherTypes(String merchant, String paymentProvider, String country);
}
