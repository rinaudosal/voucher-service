package com.docomodigital.delorean.voucher.mapper;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.model.VoucherTypes;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 2020/01/24
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Mapper(componentModel = "spring")
public interface VoucherTypeMapper extends EntityMapper<VoucherTypes, VoucherType> {

    @Override
    @Mapping(source = "amount", target = "amount.value")
    @Mapping(source = "currency", target = "amount.currency")
    @Mapping(source = "merchant", target = "merchantId")
    @Mapping(source = "shop", target = "shopId")
    VoucherType toEntity(VoucherTypes dto);

    @Override
    @InheritInverseConfiguration
    VoucherTypes toDto(VoucherType entity);
}