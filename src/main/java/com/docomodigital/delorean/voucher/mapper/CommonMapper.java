package com.docomodigital.delorean.voucher.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 2020/03/10
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Mapper(componentModel = "spring")
public interface CommonMapper {

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
