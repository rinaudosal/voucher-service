package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.domain.VoucherError;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository management for Voucher Error Collection
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherErrorRepository extends MongoRepository<VoucherError, String> {

}
