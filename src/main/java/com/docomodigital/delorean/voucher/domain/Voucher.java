package com.docomodigital.delorean.voucher.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Voucher collection
 * <p>
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Document
@CompoundIndex(def = "{'code':1, 'typeId':1}", name = "compound_index")
public class Voucher extends AbstractAuditingEntity {

    @Id
    private String id;
    @Size(min = 6, max = 60)
    private String code;
    @NotNull
    private VoucherStatus status;
    @NotNull
    private String typeId;
    private String userId;
    private String transactionId;
    private String requestId;
    private BigDecimal amount;
    private String currency;
    private Instant transactionDate;
    private Instant reserveDate;
    private Instant purchaseDate;
    private Instant redeemDate;
    private String activationUrl;
    private String voucherFileId;
    private String redeemFileId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Instant getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(Instant reserveDate) {
        this.reserveDate = reserveDate;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Instant getRedeemDate() {
        return redeemDate;
    }

    public void setRedeemDate(Instant redeemDate) {
        this.redeemDate = redeemDate;
    }

    public String getActivationUrl() {
        return activationUrl;
    }

    public void setActivationUrl(String activationUrl) {
        this.activationUrl = activationUrl;
    }

    public String getVoucherFileId() {
        return voucherFileId;
    }

    public void setVoucherFileId(String voucherFileId) {
        this.voucherFileId = voucherFileId;
    }

    public String getRedeemFileId() {
        return redeemFileId;
    }

    public void setRedeemFileId(String redeemFileId) {
        this.redeemFileId = redeemFileId;
    }
}
