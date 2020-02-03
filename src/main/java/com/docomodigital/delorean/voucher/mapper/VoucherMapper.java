package com.docomodigital.delorean.voucher.mapper;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 2020/01/29
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Mapper(componentModel = "spring")
public interface VoucherMapper extends EntityMapper<Vouchers, Voucher> {

    @Override
    @Mapping(target = "typeId", ignore = true)
    Voucher toEntity(Vouchers dto);

    @Override
    @Mapping(source = "typeId", target = "type")
    Vouchers toDto(Voucher entity);
}
