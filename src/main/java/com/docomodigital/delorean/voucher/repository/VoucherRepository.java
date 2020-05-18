package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
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
     * Method to check if exist a voucher for specified type ids
     *
     * @param transactionId the voucher transactionId
     * @param type          the types when search
     * @return true if found, false otherwise
     */
    boolean existsVoucherByTransactionIdAndTypeIdIn(String transactionId, List<String> type);

    Optional<Voucher> findByTypeIdAndTransactionId(String typeId, String transactionId);

    /**
     * Find specified voucher by code (business id)
     *
     * @param code code of the voucher
     * @return the voucher if found, false otherwise
     */
    Optional<Voucher> findByCode(String code);

    /**
     * find any voucher for specified type and status
     *
     * @param typeId        the type id requested
     * @param voucherStatus the status requested
     * @return the voucher if found
     */
    Optional<Voucher> findFirstByTypeIdAndStatusEquals(String typeId, VoucherStatus voucherStatus);

    /**
     * Method to find by code and type
     *
     * @param code the voucher code
     * @param type the voucher type id
     * @return The voucher if found
     */
    Optional<Voucher> findByCodeAndTypeId(String code, String type);

    Optional<Voucher> findByCodeAndTypeIdIn(String code, List<String> voucherTypes);

    /**
     * Find all voucher in a specific status
     *
     * @param voucherStatus the status to find
     * @return A {@link List} of the voucher founds
     */
    List<Voucher> findAllByStatus(VoucherStatus voucherStatus);
}
