package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherFile;
import com.docomodigital.delorean.voucher.domain.VoucherFileStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.mapper.VoucherFileMapper;
import com.docomodigital.delorean.voucher.repository.VoucherFileRepository;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.service.upload.VoucherSingleProcessor;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
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
@Component
public class VoucherFileServiceImpl implements VoucherFileService {
    private static final int BULK_SIZE = 1000;

    private final VoucherRepository voucherRepository;
    private final VoucherFileRepository voucherFileRepository;
    private final VoucherFileMapper voucherFileMapper;

    public VoucherFileServiceImpl(VoucherRepository voucherRepository,
                                  VoucherFileRepository voucherFileRepository,
                                  VoucherFileMapper voucherFileMapper) {
        this.voucherRepository = voucherRepository;
        this.voucherFileRepository = voucherFileRepository;
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
    public VoucherUpload uploadFile(MultipartFile file, VoucherType type, UploadOperation uploadOperation, VoucherSingleProcessor voucherSingleProcessor) {

        VoucherFile voucherUpload = new VoucherFile();
        voucherUpload.setFilename(file.getOriginalFilename());
        voucherUpload.setType(type);
        voucherUpload.setOperation(uploadOperation);
        voucherUpload.setStatus(VoucherFileStatus.UPLOADED);
        voucherFileRepository.save(voucherUpload);

        List<Voucher> vouchersToSave = new ArrayList<>();
        int total = 0;
        int uploaded = 0;
        int errors = 0;

        try {

            Scanner sc = new Scanner(file.getInputStream());
            while (sc.hasNextLine()) {
                total += 1;
                String line = sc.nextLine();

                // this change from upload purchase or redeem

                vouchersToSave.add(voucherSingleProcessor.consume(line, type, voucherUpload.getId()));

                if (vouchersToSave.size() == BULK_SIZE || !sc.hasNextLine()) {
                    voucherRepository.saveAll(vouchersToSave);
                    uploaded += vouchersToSave.size();
                    vouchersToSave = new ArrayList<>();
                }
            }
        } catch (IOException e) {
            voucherUpload.setStatus(VoucherFileStatus.ERROR);
        }

        voucherUpload.setTotal(total);
        voucherUpload.setUploaded(uploaded);
        voucherUpload.setErrors(errors);

        return voucherFileMapper.toDto(voucherUpload);
    }
}
