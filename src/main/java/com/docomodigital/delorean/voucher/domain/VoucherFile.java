package com.docomodigital.delorean.voucher.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Voucher collection
 * <p>
 * 2020/01/21
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Document
public class VoucherFile extends AbstractAuditingEntity {

    @Id
    private String id;

    @NotBlank
    private String filename;
    @NotNull
    private VoucherType type;

    @NotNull
    private VoucherFileStatus status;

    private Integer total;
    private Integer uploaded;
    private Integer errors;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public VoucherType getType() {
        return type;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public VoucherFileStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherFileStatus status) {
        this.status = status;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getUploaded() {
        return uploaded;
    }

    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
    }

    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }
}
