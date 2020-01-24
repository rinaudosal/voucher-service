package com.docomodigital.delorean.voucher.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Complex object to manage amounts, note: is not a document collection
 * <p>
 * 2020/01/22
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class Amount implements Serializable {

    private BigDecimal value;
    private String currency;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
