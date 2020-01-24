package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import org.springframework.data.mongodb.repository.MongoRepository;

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
}
