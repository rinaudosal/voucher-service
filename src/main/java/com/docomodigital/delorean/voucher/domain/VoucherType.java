package com.docomodigital.delorean.voucher.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Document
public class VoucherType extends AbstractAuditingEntity {
    public enum TYPE {
        M1, M3, M6, M12
    }

    private String id;
    private TYPE type;
    private String description;
    private String merchantCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }
}
