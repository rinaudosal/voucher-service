package com.docomodigital.delorean.domain.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    private String id;
    private String name;
    private String country;
    private String signatureKey;

}
