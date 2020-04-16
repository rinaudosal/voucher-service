package com.docomodigital.delorean.voucher.mapper;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.web.api.model.VoucherTypes;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * 2020/01/24
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface VoucherTypeMapper extends EntityMapper<VoucherTypes, VoucherType> {

    @Override
    @Mapping(source = "merchant", target = "merchantId")
    @Mapping(source = "shop", target = "shopId")
    @Mapping(source = "typeId", target = "code")
    VoucherType toEntity(VoucherTypes dto);

    @Override
    @InheritInverseConfiguration(name = "toEntity")
    VoucherTypes toDto(VoucherType entity);

    @Mapping(target = "code", ignore = true)
    @Mapping(source = "merchant", target = "merchantId")
    @Mapping(source = "shop", target = "shopId")
    void updateFromDto(VoucherTypes voucherTypes, @MappingTarget VoucherType voucherType);

}
