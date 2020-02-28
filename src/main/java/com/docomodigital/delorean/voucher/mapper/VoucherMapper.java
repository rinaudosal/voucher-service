package com.docomodigital.delorean.voucher.mapper;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 2020/01/29
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Mapper(componentModel = "spring")
public interface VoucherMapper extends EntityMapper<Vouchers, Voucher> {

    @Override
    Voucher toEntity(Vouchers dto);

    @Override
    Vouchers toDto(Voucher entity);

    default OffsetDateTime map(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.atOffset(ZoneOffset.UTC);
        }

        return null;
    }

    default LocalDateTime map(OffsetDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.toLocalDateTime();
        }

        return null;
    }
}
