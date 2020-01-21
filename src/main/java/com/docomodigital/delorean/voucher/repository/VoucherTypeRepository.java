package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherTypeRepository extends MongoRepository<VoucherType, String> {

}
