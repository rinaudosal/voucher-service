package com.docomodigital.delorean.voucher.mapper;

import com.docomodigital.delorean.voucher.BaseUnitTest;
import com.docomodigital.delorean.voucher.domain.VoucherFile;
import com.docomodigital.delorean.voucher.domain.VoucherFileStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * 2020/01/24
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherFileMapperTest extends BaseUnitTest {
    private VoucherFileMapperImpl target;

    @Before
    public void setUp() {
        target = new VoucherFileMapperImpl();
    }

    @Test
    public void singleDtoMappedCorrectly() {
        VoucherFile voucherFile = getEntity();

        VoucherUpload voucherUpload = target.toDto(voucherFile);
        assertDto(voucherUpload);
    }

    @Test
    public void nullReferencedUnMappedCorrectly() {
        Assertions.assertThat(target.toDto((VoucherFile) null)).isNull();
        Assertions.assertThat(target.toDto((List<VoucherFile>) null)).isNull();
        Assertions.assertThat(target.toEntity((VoucherUpload) null)).isNull();
        Assertions.assertThat(target.toEntity((List<VoucherUpload>) null)).isNull();
    }

    @Test
    public void listDtoMappedCorrectly() {
        VoucherFile voucherFile = getEntity();

        List<VoucherUpload> voucherUploads = target.toDto(Collections.singletonList(voucherFile));
        Assertions.assertThat(voucherUploads).hasSize(1);
        assertDto(voucherUploads.get(0));
    }

    @Test
    public void singleEntityMappedCorrectly() {
        VoucherUpload voucherUpload = getDto();

        VoucherFile voucherFile = target.toEntity(voucherUpload);
        assertEntity(voucherFile);
    }

    @Test
    public void listEntityMappedCorrectly() {
        VoucherUpload voucherType = getDto();

        List<VoucherFile> voucherTypes = target.toEntity(Collections.singletonList(voucherType));
        Assertions.assertThat(voucherTypes).hasSize(1);
        assertEntity(voucherTypes.get(0));
    }

    @Test
    public void assertStatusEnum() {
        Assertions.assertThat(target.statusEnumToVoucherFileStatus(VoucherUpload.StatusEnum.UPLOADED)).isEqualTo(VoucherFileStatus.UPLOADED);
        Assertions.assertThat(target.statusEnumToVoucherFileStatus(VoucherUpload.StatusEnum.ERROR)).isEqualTo(VoucherFileStatus.ERROR);
        Assertions.assertThat(target.statusEnumToVoucherFileStatus(null)).isNull();
    }

    @Test
    public void assertVoucherFileStatus() {
        Assertions.assertThat(target.voucherFileStatusToStatusEnum(VoucherFileStatus.UPLOADED)).isEqualTo(VoucherUpload.StatusEnum.UPLOADED);
        Assertions.assertThat(target.voucherFileStatusToStatusEnum(VoucherFileStatus.ERROR)).isEqualTo(VoucherUpload.StatusEnum.ERROR);
        Assertions.assertThat(target.voucherFileStatusToStatusEnum(null)).isNull();
    }

    @Test
    public void assertVoucherFileOperation() {
        Assertions.assertThat(target.operationEnumToUploadOperation(VoucherUpload.OperationEnum.REDEEM)).isEqualTo(UploadOperation.REDEEM);
        Assertions.assertThat(target.operationEnumToUploadOperation(VoucherUpload.OperationEnum.UPLOAD)).isEqualTo(UploadOperation.UPLOAD);
        Assertions.assertThat(target.operationEnumToUploadOperation(null)).isNull();
    }

    @Test
    public void assertVoucherUploadOperation() {
        Assertions.assertThat(target.uploadOperationToOperationEnum(UploadOperation.REDEEM)).isEqualTo(VoucherUpload.OperationEnum.REDEEM);
        Assertions.assertThat(target.uploadOperationToOperationEnum(UploadOperation.UPLOAD)).isEqualTo(VoucherUpload.OperationEnum.UPLOAD);
        Assertions.assertThat(target.uploadOperationToOperationEnum(null)).isNull();
    }


    private VoucherUpload getDto() {
        VoucherUpload voucherUpload = new VoucherUpload();
        voucherUpload.setStatus(VoucherUpload.StatusEnum.UPLOADED);
        voucherUpload.setOperation(VoucherUpload.OperationEnum.REDEEM);
        voucherUpload.setFilename("my_filename");
        voucherUpload.setTypeId("my_code");
        voucherUpload.setTotal(42);
        voucherUpload.setUploaded(42);
        voucherUpload.setErrors(42);

        return voucherUpload;
    }

    private VoucherFile getEntity() {
        VoucherFile voucherFile = new VoucherFile();
        voucherFile.setId("my_id");
        voucherFile.setFilename("my_filename");

        VoucherType voucherType = new VoucherType();
        voucherType.setCode("my_code");
        voucherFile.setType(voucherType);

        voucherFile.setStatus(VoucherFileStatus.UPLOADED);
        voucherFile.setOperation(UploadOperation.REDEEM);
        voucherFile.setTotal(42);
        voucherFile.setUploaded(42);
        voucherFile.setErrors(42);

        return voucherFile;
    }

    private void assertEntity(VoucherFile voucherFile) {
        Assertions.assertThat(voucherFile.getId()).isNull();
        Assertions.assertThat(voucherFile.getFilename()).isEqualTo("my_filename");
        Assertions.assertThat(voucherFile.getType().getCode()).isEqualTo("my_code");
        Assertions.assertThat(voucherFile.getStatus()).isEqualTo(VoucherFileStatus.UPLOADED);
        Assertions.assertThat(voucherFile.getOperation()).isEqualTo(UploadOperation.REDEEM);
        Assertions.assertThat(voucherFile.getTotal()).isEqualTo(42);
        Assertions.assertThat(voucherFile.getUploaded()).isEqualTo(42);
        Assertions.assertThat(voucherFile.getErrors()).isEqualTo(42);
    }

    private void assertDto(VoucherUpload voucherUpload) {
        Assertions.assertThat(voucherUpload.getStatus()).isEqualTo(VoucherUpload.StatusEnum.UPLOADED);
        Assertions.assertThat(voucherUpload.getOperation()).isEqualTo(VoucherUpload.OperationEnum.REDEEM);
        Assertions.assertThat(voucherUpload.getFilename()).isEqualTo("my_filename");
        Assertions.assertThat(voucherUpload.getTypeId()).isEqualTo("my_code");
        Assertions.assertThat(voucherUpload.getTotal()).isEqualTo(42);
        Assertions.assertThat(voucherUpload.getUploaded()).isEqualTo(42);
        Assertions.assertThat(voucherUpload.getErrors()).isEqualTo(42);
    }

}
