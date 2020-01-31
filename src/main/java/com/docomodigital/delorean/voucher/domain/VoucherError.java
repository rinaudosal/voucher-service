package com.docomodigital.delorean.voucher.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

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

    @NotBlank
    private String uploadId;

    @NotBlank
    private String code;

    private String line;

    private String errorCode;

    private String errorMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
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
