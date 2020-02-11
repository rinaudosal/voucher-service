package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherConsumer;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.service.VoucherTypeService;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 2020/02/07
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Service
public class ConsumeVoucherServiceImpl implements ConsumeVoucherService {

    private final VoucherTypeService voucherTypeService;
    private final VoucherRepository voucherRepository;
    private final Clock clock;

    public ConsumeVoucherServiceImpl(VoucherTypeService voucherTypeService, VoucherRepository voucherRepository, Clock clock) {
        this.voucherTypeService = voucherTypeService;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
    }

    @Override
    public VoucherConsumer readMessage(String message) {
        Configuration conf = Configuration.defaultConfiguration()
            .addOptions(Option.SUPPRESS_EXCEPTIONS);

        DocumentContext jsonContext = JsonPath.using(conf).parse(message);
        VoucherConsumer voucherConsumer = new VoucherConsumer();
        voucherConsumer.setMerchantId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['product'].['attributes'].['merchantCode']"));
        voucherConsumer.setPaymentProvider(jsonContext.read("$['attributes'].['transaction'].['attributes'].['telco'].['attributes'].['code']"));
        voucherConsumer.setProductId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['product'].['type']"));

        voucherConsumer.setCountry(jsonContext.read("$['attributes'].['transaction'].['attributes'].['product'].['attributes'].['country'].['attributes'].['code']"));
        voucherConsumer.setUserId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['userId'].['attributes'].['customerId']"));
        voucherConsumer.setTransactionId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['transactionCode']"));
        voucherConsumer.setBillingStatus(jsonContext.read("$['attributes'].['transaction'].['attributes'].['billingStatus']"));

        String transactionDateString = jsonContext.read("$['attributes'].['transaction'].['attributes'].['dateLastUpdated']");
        if (StringUtils.isNotBlank(transactionDateString)) {
            voucherConsumer.setTransactionDate(LocalDateTime.parse(transactionDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return voucherConsumer;
    }

    @Override
    public Voucher consumeVoucher(VoucherConsumer voucherConsumer) {
        if (!voucherConsumer.getBillingStatus().equals("BILLED")) {
            throw new BadRequestException("REQUEST_NOT_BILLED", "Wrong type of request, status must be BILLED, request status is " + voucherConsumer.getBillingStatus());
        }

        VoucherType voucherType = Optional.ofNullable(voucherTypeService.getVoucherType(
            voucherConsumer.getMerchantId(),
            voucherConsumer.getPaymentProvider(),
            voucherConsumer.getCountry(),
            voucherConsumer.getProductId()))
            .orElseThrow(() -> new BadRequestException("TYPE_NOT_FOUND", "Voucher type not found"));

        Voucher voucherToBeConsume = voucherRepository.findFirstByTypeIdAndStatusEquals(voucherType.getId(), VoucherStatus.ACTIVE)
            .orElseThrow(() -> new BadRequestException("VOUCHER_NOT_FOUND", "Voucher with type " + voucherType.getId() + " and status ACTIVE not found"));

        voucherToBeConsume.setStatus(VoucherStatus.PURCHASED);
        voucherToBeConsume.setUserId(voucherConsumer.getUserId());
        voucherToBeConsume.setTransactionId(voucherConsumer.getTransactionId());
        voucherToBeConsume.setTransactionDate(voucherConsumer.getTransactionDate());
        voucherToBeConsume.setPurchaseDate(LocalDate.now(clock));
        voucherToBeConsume.setActivationUrl(voucherType.getBaseUrl() + voucherToBeConsume.getCode());

        return voucherRepository.save(voucherToBeConsume);
    }

    @Override
    public void sendNotification(Voucher voucher) {
        // nothing to do
    }
}
