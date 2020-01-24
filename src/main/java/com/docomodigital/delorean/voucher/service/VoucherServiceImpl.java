package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherRepository voucherRepository;
    private final Clock clock;

    public VoucherServiceImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
    }

    @Override
    public List<AvailableVoucherTypes> getAvailableVoucherTypes(String merchant, String paymentProvider, String country) {

        // fina all voucher type for the merchant, enabled and in correct range date
        VoucherType voucherType = new VoucherType();
        voucherType.setMerchantId(merchant);
        voucherType.setPaymentProvider(paymentProvider);
        voucherType.setCountry(country);
        voucherType.setEnabled(true);
        voucherType.setCreatedDate(null);
        voucherType.setLastModifiedDate(null);

        Example<VoucherType> voucherTypeExample = Example.of(voucherType);
        return voucherTypeRepository.findAll(voucherTypeExample)
            .stream()
            .filter(vou -> {
                LocalDate now = LocalDate.now(clock);
                return now.isBefore(vou.getEndDate()) && now.isAfter(vou.getStartDate());
            })
            .map(v -> {
                AvailableVoucherTypes availableVoucherTypes = new AvailableVoucherTypes();
                availableVoucherTypes.setCode(v.getCode());
                availableVoucherTypes.setDescription(v.getDescription());
                availableVoucherTypes.setAmount(v.getAmount().getValue());
                availableVoucherTypes.setCurrency(v.getAmount().getCurrency());

                // count the voucher ACTIVE
                Voucher voucher = new Voucher();
                voucher.setStatus(VoucherStatus.ACTIVE);
                voucher.setType(v);
                voucher.setCreatedDate(null);
                voucher.setLastModifiedDate(null);
                availableVoucherTypes.setVoucherAvailable((int) voucherRepository.count(Example.of(voucher)));
                return availableVoucherTypes;
            })
            .filter(v -> v.getVoucherAvailable() > 0)
            .sorted(Comparator.comparing(AvailableVoucherTypes::getCode))//only voucher available
            .collect(Collectors.toList());
    }
}
