package com.docomodigital.delorean.client.merchant.model;

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
    private boolean requireSignedSession;
    private String contractId;

}
