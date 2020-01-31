package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.domain.VoucherFile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository management for Voucher File Collection
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherFileRepository extends MongoRepository<VoucherFile, String> {

}
