package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.*;
import com.docomodigital.delorean.voucher.mapper.VoucherFileMapper;
import com.docomodigital.delorean.voucher.repository.VoucherFileRepository;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import org.springframework.stereotype.Component;
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
public class VoucherFileComponent {
    private static final int BULK_SIZE = 1000;

    private final VoucherRepository voucherRepository;
    private final VoucherFileRepository voucherFileRepository;
    private final VoucherFileMapper voucherFileMapper;

    public VoucherFileComponent(VoucherRepository voucherRepository, VoucherFileRepository voucherFileRepository, VoucherFileMapper voucherFileMapper) {
        this.voucherRepository = voucherRepository;
        this.voucherFileRepository = voucherFileRepository;
        this.voucherFileMapper = voucherFileMapper;
    }

    /**
     * Check the file that want to upload
     *
     * @param file the multipart file
     */
    public void checkFileToUpload(MultipartFile file) {
        // must to be text/plain
        if (!Objects.equals(file.getContentType(), "text/csv")) {
            throw new BadRequestException("FILE_MALFORMED", "Error, the file is malformed");
        }
    }

    public VoucherUpload uploadFile(MultipartFile file, VoucherType voucherType) throws IOException {
        VoucherFile voucherUpload = new VoucherFile();
        voucherUpload.setFilename(file.getOriginalFilename());
        voucherUpload.setType(voucherType);
        voucherUpload.setStatus(VoucherFileStatus.UPLOADED);
        voucherFileRepository.save(voucherUpload);

        //ho il file in memoria, leggo 100 righe alla volta, ogni cento righe salvo a db e svuoto le collection
        List<Voucher> vouchersToSave = new ArrayList<>();
        int total = 0;
        int uploaded = 0;
        int errors = 0;

        Scanner sc = new Scanner(file.getInputStream());
        while (sc.hasNextLine()) {
            total += 1;
            String line = sc.nextLine();
            vouchersToSave.add(this.buildVoucher(line, voucherType, voucherUpload.getId()));

            if (vouchersToSave.size() == BULK_SIZE || !sc.hasNextLine()) {
                voucherRepository.saveAll(vouchersToSave);
                uploaded += vouchersToSave.size();
                vouchersToSave = new ArrayList<>();
            }
        }

        voucherUpload.setTotal(total);
        voucherUpload.setUploaded(uploaded);
        voucherUpload.setErrors(errors);

        return voucherFileMapper.toDto(voucherUpload);
    }

    private Voucher buildVoucher(String code, VoucherType voucherType, String uploadId) {
        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setType(voucherType);
        voucher.setUploadId(uploadId);

        return voucher;
    }
}
