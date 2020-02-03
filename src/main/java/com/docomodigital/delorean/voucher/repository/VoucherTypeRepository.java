package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherTypeRepository extends MongoRepository<VoucherType, String> {

    /**
     * Find specified voucher type by code (business id)
     *
     * @param code code of the voucher type
     * @return the voucher type if found, false otherwise
     */
    Optional<VoucherType> findByCode(String code);

    /**
     * Retrieve if the type by code (business id) exist or not
     *
     * @param code code of the voucher type
     * @return true if the voucher type if found, false otherwise
     */
    boolean existsVoucherTypeByCode(String code);

    /**
     * Retrieve if the type by code (business id) exist or not
     *
     * @param product  product category of the voucher type
     * @param priority the priority of the voucher type
     * @return true if the voucher type if found, false otherwise
     */
    boolean existsVoucherTypeByProductAndPriority(String product, Integer priority);

    /**
     * Method to retrieve the ids of voucher types that have the same merchant id
     *
     * @param merchantId the merchant id
     * @return List of ids of the types that have input merchant id
     */
    List<VoucherType> findAllByMerchantId(String merchantId);

}
