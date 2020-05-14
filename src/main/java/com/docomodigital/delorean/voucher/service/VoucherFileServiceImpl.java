package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.*;
import com.docomodigital.delorean.voucher.mapper.VoucherFileMapper;
import com.docomodigital.delorean.voucher.repository.VoucherErrorRepository;
import com.docomodigital.delorean.voucher.repository.VoucherFileRepository;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.service.upload.RedeemVoucherComponent;
import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.service.upload.UploadVoucherComponent;
import com.docomodigital.delorean.voucher.service.upload.VoucherSingleProcessor;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRedeem;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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
    private final UploadVoucherComponent uploadVoucherStrategy;
    private final RedeemVoucherComponent redeemVoucherComponent;

    public VoucherFileServiceImpl(VoucherRepository voucherRepository,
                                  VoucherFileRepository voucherFileRepository,
                                  VoucherErrorRepository voucherErrorRepository,
                                  VoucherFileMapper voucherFileMapper,
                                  UploadVoucherComponent uploadVoucherStrategy,
                                  RedeemVoucherComponent redeemVoucherComponent) {
        this.voucherRepository = voucherRepository;
        this.voucherFileRepository = voucherFileRepository;
        this.voucherErrorRepository = voucherErrorRepository;
        this.voucherFileMapper = voucherFileMapper;
        this.uploadVoucherStrategy = uploadVoucherStrategy;
        this.redeemVoucherComponent = redeemVoucherComponent;
    }

    @Override
    public void checkFileToUpload(MultipartFile file) {
        // must to be text/plain
        if (!Objects.equals(file.getContentType(), "text/csv")) {
            throw new BadRequestException("FILE_MALFORMED", "Error, the file is malformed");
        }
    }

    @Override
    public VoucherRedeem redeemFile(MultipartFile file, List<VoucherType> voucherTypes) {
        VoucherFile voucherFile = saveVoucherFile(file.getOriginalFilename(), null, UploadOperation.REDEEM);

        saveData(file, voucherTypes, voucherFile, true, redeemVoucherComponent::processLine);

        return voucherFileMapper.toRedeemDto(voucherFile);
    }

    @Override
    @Transactional
    public VoucherUpload uploadFile(MultipartFile file, VoucherType type) {

        VoucherFile voucherFile = saveVoucherFile(file.getOriginalFilename(), type, UploadOperation.UPLOAD);

        saveData(file, Collections.singletonList(type), voucherFile, false, uploadVoucherStrategy::processLine);

        return voucherFileMapper.toDto(voucherFile);
    }

    private void saveData(MultipartFile file, List<VoucherType> voucherTypes, VoucherFile voucherFile, boolean skipFirstLine, VoucherSingleProcessor<String, List<VoucherType>, Voucher> voucherSingleProcessor) {
        List<Voucher> vouchersToSave = new ArrayList<>();
        int lineNumber = 0;
        int uploaded = 0;
        int errors = 0;

        try {

            Scanner sc = new Scanner(file.getInputStream());
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lineNumber += 1;

                if (lineNumber == 1 && skipFirstLine) continue;

                errors = checkSingleLine(voucherTypes, voucherFile, voucherSingleProcessor, vouchersToSave, lineNumber, errors, line);

                if (vouchersToSave.size() == BULK_SIZE || !sc.hasNextLine()) {
                    voucherRepository.saveAll(vouchersToSave);
                    uploaded += vouchersToSave.size();
                    vouchersToSave = new ArrayList<>();
                }
            }
        } catch (IOException e) {
            log.error("Exception on read file", e);
            voucherFile.setStatus(VoucherFileStatus.ERROR);
        }

        voucherFile.setTotal(skipFirstLine ? lineNumber - 1 : lineNumber);
        voucherFile.setUploaded(uploaded);
        voucherFile.setErrors(errors);
    }

    private int checkSingleLine(List<VoucherType> voucherTypes, VoucherFile voucherFile, VoucherSingleProcessor<String, List<VoucherType>, Voucher> voucherSingleProcessor, List<Voucher> vouchersToSave, int lineNumber, int errors, String line) {
        try {
            vouchersToSave.add(voucherSingleProcessor.consume(line, voucherTypes, voucherFile.getId()));
        } catch (BadRequestException e) {
            log.error("Error on process line " + lineNumber + " with error " + e.getErrorCode(), e);

            errors += 1;
            VoucherError voucherError = new VoucherError();
            voucherError.setVoucherFileId(voucherFile.getId());
            voucherError.setLine(line);
            voucherError.setLineNumber(lineNumber);
            voucherError.setErrorCode(e.getErrorCode());
            voucherError.setErrorMessage(e.getMessage());
            voucherErrorRepository.save(voucherError);
        }
        return errors;
    }

    private VoucherFile saveVoucherFile(String filename, VoucherType type, UploadOperation operation) {
        VoucherFile voucherFile = new VoucherFile();
        voucherFile.setFilename(filename);
        voucherFile.setType(type);
        voucherFile.setOperation(operation);
        voucherFile.setStatus(VoucherFileStatus.UPLOADED);
        return voucherFileRepository.save(voucherFile);
    }


}
