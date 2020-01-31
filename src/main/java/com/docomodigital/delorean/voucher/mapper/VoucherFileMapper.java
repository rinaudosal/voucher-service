package com.docomodigital.delorean.voucher.mapper;

import com.docomodigital.delorean.voucher.domain.VoucherFile;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 2020/01/29
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Mapper(componentModel = "spring")
public interface VoucherFileMapper extends EntityMapper<VoucherUpload, VoucherFile> {

    @Override
    @Mapping(target = "type", ignore = true)
    VoucherFile toEntity(VoucherUpload dto);

    @Override
    @Mapping(source = "type.code", target = "type")
    VoucherUpload toDto(VoucherFile entity);
}
