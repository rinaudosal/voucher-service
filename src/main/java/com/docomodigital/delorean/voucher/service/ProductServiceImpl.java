package com.docomodigital.delorean.voucher.service;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2020/02/24
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherRepository voucherRepository;
    private final Clock clock;

    public ProductServiceImpl(VoucherTypeRepository voucherTypeRepository, VoucherRepository voucherRepository, Clock clock) {
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
    }

    @Override
    public List<String> getAvailableProducts(List<String> products) {
        // find all voucher type for the merchant, enabled and in correct range date
        return voucherTypeRepository.findAllVoucherTypeByProductIn(products)
            .stream()
            .filter(vou -> {
            	LocalDateTime now = LocalDateTime.now(clock);
                int voucherAvailable = getVoucherAvailable(vou);

                return now.isBefore(vou.getEndDate()) && now.isAfter(vou.getStartDate().minusDays(1))
                    && voucherAvailable > 0
                    && vou.getEnabled();
            })
            .map(VoucherType::getProduct).distinct()
            .collect(Collectors.toList());

    }

    private int getVoucherAvailable(VoucherType v) {
        Voucher voucher = new Voucher();
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTypeId(v.getId());
        voucher.setCreatedDate(null);
        voucher.setLastModifiedDate(null);
        return (int) voucherRepository.count(Example.of(voucher));
    }
}
