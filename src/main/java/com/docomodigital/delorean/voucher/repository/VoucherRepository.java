package com.docomodigital.delorean.voucher.repository;

import com.docomodigital.delorean.voucher.domain.Voucher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository management for Voucher Collection
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface VoucherRepository extends MongoRepository<Voucher, String> {

    boolean existsVoucherByCodeAndTypeIdIn(String code, List<String> type);

}
