package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.domain.Voucher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository management for Voucher Collection
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherRepository extends MongoRepository<Voucher, String> {

    /**
     * Method to check if exist a voucher for specified type ids
     *
     * @param code the voucher code to find
     * @param type the types when search
     * @return true if found, false otherwise
     */
    boolean existsVoucherByCodeAndTypeIdIn(String code, List<String> type);

    /**
     * Find specified voucher by code (business id)
     *
     * @param code code of the voucher
     * @return the voucher if found, false otherwise
     */
    Optional<Voucher> findByCode(String code);

}
