package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;
import com.docomodigital.delorean.voucher.web.api.model.ReserveRequest;
import com.docomodigital.delorean.voucher.web.api.model.VoucherTypes;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;

/**
 * Business class that manage the voucher types
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherTypeService {


    /**
     * Method that retrieve the available products
     *
     * @param merchant        the merchant owner of the vouchers
     * @param paymentProvider the payment provider that distribute the vouchers
     * @param country         the country where the voucher are distributed
     * @return the List of {@link AvailableVoucherTypes} if founds, empty otherwise
     */
    List<AvailableVoucherTypes> getAvailableVoucherTypes(String merchant, String paymentProvider, String country);

    /**
     * Restrieve a {@link List} of VoucherTypes by example request
     *
     * @param example {@link Example} voucher data model
     * @return List of result filtered
     */
    List<VoucherTypes> getVoucherTypes(Example<VoucherType> example);

    /**
     * Retrieve the voucher type by code (unique)
     *
     * @param code the code of the voucher type id
     * @return the voucher type if found, false otherwise
     */
    Optional<VoucherTypes> getVoucherType(String code);

    /**
     * Create a single voucher type by input parameters
     *
     * @param voucherTypes the voucher type to create
     * @return the voucher type created
     */
    VoucherTypes createVoucherType(VoucherTypes voucherTypes);

    /**
     * Update the voucher type by input parameters
     *
     * @param code         logical id of the voucher type to update
     * @param voucherTypes the voucher type to update
     * @return the voucher type updated
     */
    Optional<VoucherTypes> updateVoucherType(String code, VoucherTypes voucherTypes);

    /**
     * Retrieve voucher type with the highest priority in current day of the request
     *
     * @param merchantId      the merchant id
     * @param paymentProvider the payment provider
     * @param country         the country
     * @param productId       the product id
     * @return the voucher type if found
     */
    VoucherType getVoucherType(String merchantId, String paymentProvider, String country, String productId);

    Optional<Vouchers> reserveVoucher(String typeId, ReserveRequest reserveRequest);

    /**
     * Find a voucherType if found
     *
     * @param id the type id
     * @return VoucherType object if found
     */
    VoucherType findById(String id);
}
