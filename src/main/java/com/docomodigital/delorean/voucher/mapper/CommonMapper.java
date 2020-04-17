package com.docomodigital.delorean.voucher.mapper;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 2020/03/10
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Mapper(componentModel = "spring")
public interface CommonMapper {

    default OffsetDateTime map(Instant instant) {
        if (instant != null) {
            return instant.atOffset(ZoneOffset.UTC);
        }

        return null;
    }

    default Instant map(OffsetDateTime offsetDateTime) {
        if (offsetDateTime != null) {
            return offsetDateTime.toInstant();
        }

        return null;
    }

}
