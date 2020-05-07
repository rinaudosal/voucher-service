package com.docomodigital.delorean.voucher.service.upload;

import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherConsumer;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.service.VoucherTypeService;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 2020/02/07
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Service
public class ConsumeVoucherServiceImpl implements ConsumeVoucherService {

    private final VoucherTypeService voucherTypeService;
    private final VoucherRepository voucherRepository;
    private final Clock clock;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ConsumeVoucherServiceImpl(VoucherTypeService voucherTypeService,
                                     VoucherRepository voucherRepository,
                                     Clock clock,
                                     RabbitTemplate rabbitTemplate,
                                     ObjectMapper objectMapper) {
        this.voucherTypeService = voucherTypeService;
        this.voucherRepository = voucherRepository;
        this.clock = clock;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public VoucherConsumer readMessage(String message) {
        Configuration conf = Configuration.defaultConfiguration()
            .addOptions(Option.SUPPRESS_EXCEPTIONS);

        DocumentContext jsonContext = JsonPath.using(conf).parse(message);
        VoucherConsumer voucherConsumer = new VoucherConsumer();
        voucherConsumer.setMerchantId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['product'].['attributes'].['merchantCode']"));
        voucherConsumer.setShopId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['product'].['attributes'].['siteCode']"));
        voucherConsumer.setPaymentProvider(jsonContext.read("$['attributes'].['transaction'].['attributes'].['telco'].['attributes'].['code']"));
        voucherConsumer.setProductId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['product'].['attributes'].['code']"));

        voucherConsumer.setCountry(jsonContext.read("$['attributes'].['transaction'].['attributes'].['product'].['attributes'].['country'].['attributes'].['code']"));
        voucherConsumer.setUserId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['userId'].['attributes'].['customerId']"));
        voucherConsumer.setTransactionId(jsonContext.read("$['attributes'].['transaction'].['attributes'].['transactionCode']"));
        voucherConsumer.setRequestId(jsonContext.read("$['attributes'].['code']"));
        voucherConsumer.setBillingStatus(jsonContext.read("$['attributes'].['transaction'].['attributes'].['status']"));

        String transactionDateString = jsonContext.read("$['attributes'].['transaction'].['attributes'].['dateLastUpdated']");
        if (StringUtils.isNotBlank(transactionDateString)) {
            voucherConsumer.setTransactionDate(LocalDateTime.parse(transactionDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toInstant(ZoneOffset.UTC));
        }

        return voucherConsumer;
    }

    //FIXME CONFIGURE ATOMIC CALL
    @Override
    public Voucher consumeVoucher(VoucherConsumer voucherConsumer) {
        if (!"BILLED".equals(voucherConsumer.getBillingStatus())) {
            throw new BadRequestException("REQUEST_NOT_BILLED", "Wrong type of request, status must be BILLED, request status is " + voucherConsumer.getBillingStatus());
        }

        VoucherType voucherType = Optional.ofNullable(voucherTypeService.getVoucherType(
            voucherConsumer.getShopId(),
            voucherConsumer.getPaymentProvider(),
            voucherConsumer.getCountry(),
            voucherConsumer.getProductId()))
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, "Voucher Type not found"));

        Voucher voucherToBeConsume = voucherRepository.findFirstByTypeIdAndStatusEquals(voucherType.getId(), VoucherStatus.ACTIVE)
            .orElseThrow(() -> new BadRequestException(Constants.VOUCHER_NOT_FOUND_ERROR, "Voucher with type " + voucherType.getId() + " and status ACTIVE not found"));

        voucherToBeConsume.setStatus(VoucherStatus.PURCHASED);
        voucherToBeConsume.setUserId(voucherConsumer.getUserId());
        voucherToBeConsume.setTransactionId(voucherConsumer.getTransactionId());
        voucherToBeConsume.setRequestId(voucherConsumer.getRequestId());
        voucherToBeConsume.setTransactionDate(voucherConsumer.getTransactionDate());
        voucherToBeConsume.setPurchaseDate(Instant.now(clock));
        voucherToBeConsume.setActivationUrl(voucherType.getBaseUrl() + voucherToBeConsume.getCode());

        return voucherRepository.save(voucherToBeConsume);
    }

    @Override
    public void sendNotification(Voucher voucher) throws Exception {
        String voucherString = objectMapper.writeValueAsString(voucher);
        log.info(String.format("Sending response %s to tinder-plugin2api queue...", voucherString));
        rabbitTemplate.convertAndSend("tinder-plugin2api", voucherString, m -> {
            m.getMessageProperties().getHeaders().put("pluginCallCode", voucher.getRequestId());
            return m;
        });
        log.info("Message sent successfully");
    }
}
