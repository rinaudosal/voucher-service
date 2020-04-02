package com.docomodigital.delorean.voucher.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Voucher collection
 * <p>
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Document
public class VoucherError extends AbstractAuditingEntity {

    @Id
    private String id;

    private String voucherFileId;

    private String line;

    private Integer lineNumber;

    private String errorCode;

    private String errorMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoucherFileId() {
        return voucherFileId;
    }

    public void setVoucherFileId(String voucherFileId) {
        this.voucherFileId = voucherFileId;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
