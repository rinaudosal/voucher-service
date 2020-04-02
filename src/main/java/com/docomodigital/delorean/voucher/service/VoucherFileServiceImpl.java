package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.*;
import com.docomodigital.delorean.voucher.mapper.VoucherFileMapper;
import com.docomodigital.delorean.voucher.repository.VoucherErrorRepository;
import com.docomodigital.delorean.voucher.repository.VoucherFileRepository;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.service.upload.VoucherSingleProcessor;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Utility class to manage Voucher File (check & upload)
 * 2020/01/30
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Component
public class VoucherFileServiceImpl implements VoucherFileService {
    private static final int BULK_SIZE = 1000;

    private final VoucherRepository voucherRepository;
    private final VoucherFileRepository voucherFileRepository;
    private final VoucherErrorRepository voucherErrorRepository;
    private final VoucherFileMapper voucherFileMapper;

    public VoucherFileServiceImpl(VoucherRepository voucherRepository,
                                  VoucherFileRepository voucherFileRepository,
                                  VoucherErrorRepository voucherErrorRepository,
                                  VoucherFileMapper voucherFileMapper) {
        this.voucherRepository = voucherRepository;
        this.voucherFileRepository = voucherFileRepository;
        this.voucherErrorRepository = voucherErrorRepository;
        this.voucherFileMapper = voucherFileMapper;
    }

    @Override
    public void checkFileToUpload(MultipartFile file) {
        // must to be text/plain
        if (!Objects.equals(file.getContentType(), "text/csv")) {
            throw new BadRequestException("FILE_MALFORMED", "Error, the file is malformed");
        }
    }

    @Override
    @Transactional
    public VoucherUpload uploadFile(MultipartFile file, VoucherType type, UploadOperation uploadOperation, VoucherSingleProcessor<String, VoucherType, Voucher> voucherSingleProcessor, boolean skipHeaderLine) {

        VoucherFile voucherUpload = new VoucherFile();
        voucherUpload.setFilename(file.getOriginalFilename());
        voucherUpload.setType(type);
        voucherUpload.setOperation(uploadOperation);
        voucherUpload.setStatus(VoucherFileStatus.UPLOADED);
        voucherFileRepository.save(voucherUpload);

        List<Voucher> vouchersToSave = new ArrayList<>();
        int lineNumber = 0;
        int uploaded = 0;
        int errors = 0;

        try {

            Scanner sc = new Scanner(file.getInputStream());
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lineNumber += 1;

                if (lineNumber == 1 && skipHeaderLine) continue;


                // this change from upload purchase or redeem

                errors = checkSingleLine(type, voucherSingleProcessor, voucherUpload, vouchersToSave, lineNumber, errors, line);

                if (vouchersToSave.size() == BULK_SIZE || !sc.hasNextLine()) {
                    voucherRepository.saveAll(vouchersToSave);
                    uploaded += vouchersToSave.size();
                    vouchersToSave = new ArrayList<>();
                }
            }
        } catch (IOException e) {
            log.error("Exception on read file", e);
            voucherUpload.setStatus(VoucherFileStatus.ERROR);
        }

        voucherUpload.setTotal(skipHeaderLine ? lineNumber - 1 : lineNumber);
        voucherUpload.setUploaded(uploaded);
        voucherUpload.setErrors(errors);

        return voucherFileMapper.toDto(voucherUpload);
    }

    private int checkSingleLine(VoucherType type,
                                VoucherSingleProcessor<String, VoucherType, Voucher> voucherSingleProcessor,
                                VoucherFile voucherUpload,
                                List<Voucher> vouchersToSave,
                                int lineNumber,
                                int errors,
                                String line) {
        try {
            Voucher voucherProcessed = voucherSingleProcessor.consume(line, type, voucherUpload.getId());
            vouchersToSave.add(voucherProcessed);
        } catch (BadRequestException e) {
            log.error("Error on process line " + lineNumber + " with error " + e.getErrorCode(), e);

            errors += 1;
            VoucherError voucherError = new VoucherError();
            voucherError.setVoucherFileId(voucherUpload.getId());
            voucherError.setLine(line);
            voucherError.setLineNumber(lineNumber);
            voucherError.setErrorCode(e.getErrorCode());
            voucherError.setErrorMessage(e.getMessage());
            voucherErrorRepository.save(voucherError);
        }
        return errors;
    }
}
