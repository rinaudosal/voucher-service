package com.docomodigital.delorean.voucher.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.docomodigital.delorean.domain.resource.Shop;
import com.docomodigital.delorean.voucher.config.Constants;
import com.docomodigital.delorean.voucher.domain.Voucher;
import com.docomodigital.delorean.voucher.domain.VoucherStatus;
import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.mapper.VoucherMapper;
import com.docomodigital.delorean.voucher.repository.VoucherRepository;
import com.docomodigital.delorean.voucher.repository.VoucherTypeRepository;
import com.docomodigital.delorean.voucher.service.upload.ProcessVoucherFactory;
import com.docomodigital.delorean.voucher.service.upload.ProcessVoucherStrategy;
import com.docomodigital.delorean.voucher.service.upload.UploadOperation;
import com.docomodigital.delorean.voucher.web.api.error.BadRequestException;
import com.docomodigital.delorean.voucher.web.api.model.VoucherRequest;
import com.docomodigital.delorean.voucher.web.api.model.VoucherUpload;
import com.docomodigital.delorean.voucher.web.api.model.Vouchers;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import net.netm.billing.library.AccountingConnection;
import net.netm.billing.library.exception.AccountingException;
import net.netm.billing.library.exception.CDRValidationException;
import net.netm.billing.library.model.CDR;


/**
 * 2020/01/29
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Service
public class VoucherServiceImpl implements VoucherService {
	
	private final VoucherRepository voucherRepository;
    private final VoucherTypeRepository voucherTypeRepository;
    private final VoucherFileService voucherFileService;
    private final VoucherMapper voucherMapper;
    private final Clock clock;
    private final ProcessVoucherFactory uploadFileFactory;
    private AccountingConnection accsrv;

    public VoucherServiceImpl(VoucherRepository voucherRepository,
                              VoucherTypeRepository voucherTypeRepository,
                              VoucherFileService voucherFileService,
                              VoucherMapper voucherMapper,
                              Clock clock,
                              ProcessVoucherFactory uploadFileFactory
    ) {
        this.voucherRepository = voucherRepository;
        this.voucherTypeRepository = voucherTypeRepository;
        this.voucherFileService = voucherFileService;
        this.voucherMapper = voucherMapper;
        this.clock = clock;
        this.uploadFileFactory = uploadFileFactory;
        this.accsrv = new AccountingConnection();
    }

    @Override
    public VoucherUpload processVouchers(MultipartFile file, String type, UploadOperation uploadOperation) {

        voucherFileService.checkFileToUpload(file);

        ProcessVoucherStrategy processVoucherStrategy = uploadFileFactory.getUploadFileStrategy(uploadOperation);

        VoucherType voucherType = processVoucherStrategy.getValidVoucherType(type);

        return voucherFileService.uploadFile(file, voucherType, uploadOperation, processVoucherStrategy::processLine, processVoucherStrategy.skipHeaderLine());
    }

    @Override
    public List<Vouchers> getVouchers(String typeCode, String status, String userId, String merchantId, String transactionId) {
        Voucher voucher = new Voucher();
        voucher.setUserId(StringUtils.trimToNull(userId));
        voucher.setTransactionId(StringUtils.trimToNull(transactionId));
        if (StringUtils.isNotBlank(typeCode)) {

            VoucherType type = voucherTypeRepository.findByCode(typeCode)
                .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, typeCode)));

            if (StringUtils.isBlank(merchantId) || type.getMerchantId().equals(merchantId)) {
                voucher.setTypeId(StringUtils.trimToNull(type.getId()));
            } else {
                throw new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, typeCode));
            }
        }

        if (StringUtils.isNotBlank(status)) {
            if (!EnumUtils.isValidEnum(VoucherStatus.class, status)) {
                throw new BadRequestException(Constants.WRONG_STATUS_ERROR, String.format("Status %s is wrong", status));
            }
            voucher.setStatus(VoucherStatus.valueOf(status));
        }

        voucher.setCreatedDate(null);
        voucher.setLastModifiedDate(null);
        Example<Voucher> voucherExample = Example.of(voucher);

        return voucherRepository.findAll(voucherExample).stream()
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(voucherTypeRepository.findById(v.getTypeId()).map(VoucherType::getCode).orElse(null));
                return vouchers;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Vouchers> updateVoucher(String code, String typeId, VoucherRequest voucherRequest) {
        VoucherType voucherType = voucherTypeRepository.findByCode(typeId)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, typeId)));

        // User not enabled to reserve
        Shop shop = (Shop) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!shop.getId().equalsIgnoreCase(voucherType.getShopId())) {
            throw new BadRequestException(Constants.UNAUTHORIZED_SHOP_NAME,
                String.format("The shop %s is not enable to reserve vouchers of %s",
                    shop.getId(),
                    voucherType.getShopId()));
        }

        if (!voucherType.getEnabled()) {
            throw new BadRequestException(Constants.TYPE_DISABLED_ERROR, String.format("Voucher Type %s is disabled", typeId));
        }

        Voucher voucher = voucherRepository.findByCode(code)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format("Voucher %s not found for type %s", code, typeId)));

        if (!VoucherStatus.RESERVED.equals(voucher.getStatus())) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR, String.format("Voucher with code %s is not in RESERVED state", code));
        }

        if (!voucherRequest.getTransactionId().equalsIgnoreCase(voucher.getTransactionId())) {
            throw new BadRequestException(Constants.WRONG_TRANSACTION_ID_ERROR,
                String.format("Transaction id %s is different of reserved %s",
                    voucherRequest.getTransactionId(),
                    voucher.getTransactionId()));
        }

        if (VoucherRequest.TransactionStatusEnum.SUCCESS.equals(voucherRequest.getTransactionStatus())) {
            voucher.setTransactionId(voucherRequest.getTransactionId());
            if (voucherRequest.getTransactionDate() != null) {
                voucher.setTransactionDate(voucherRequest.getTransactionDate().toLocalDateTime());
            }
            voucher.setStatus(VoucherStatus.PURCHASED);
            voucher.setPurchaseDate(LocalDateTime.now(clock));
            voucher.setAmount(voucherRequest.getAmount());
            voucher.setCurrency(voucherRequest.getCurrency());
            voucher.setUserId(voucherRequest.getUserId());
            try {
            	CDR cdr = createCdr(voucher,voucherType,shop.getContractId());
				if(cdr != null) {
					accsrv.chargeOne(cdr);					
				}
            } catch (AccountingException e) {
            	e.printStackTrace();
            }
        } else {
            voucher.setTransactionId(null);
            voucher.setTransactionDate(null);
            voucher.setAmount(null);
            voucher.setCurrency(null);
            voucher.setStatus(VoucherStatus.ACTIVE);
            voucher.setPurchaseDate(null);
            voucher.setReserveDate(null);
            voucher.setUserId(null);
            voucher.setActivationUrl(null);
        }
        

        return Optional.of(voucherRepository.save(voucher))
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(typeId);

                return vouchers;
            });
    }

    @Override
    public Optional<Vouchers> getVoucher(String code, String typeId) {
        VoucherType voucherType = voucherTypeRepository.findByCode(typeId)
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format(Constants.VOUCHER_TYPE_NOT_FOUND_MESSAGE, typeId)));

        // User not enabled to reserve
        Shop shop = (Shop) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!shop.getId().equalsIgnoreCase(voucherType.getShopId())) {
            throw new BadRequestException(Constants.UNAUTHORIZED_SHOP_NAME,
                String.format("The shop %s is not enable to get vouchers of %s",
                    shop.getId(),
                    voucherType.getShopId()));
        }

        Voucher voucher = voucherRepository.findByCodeAndTypeId(code, voucherType.getId())
            .orElseThrow(() -> new BadRequestException(Constants.TYPE_NOT_FOUND_ERROR, String.format("Voucher %s not found for type %s", code, typeId)));

        if (!(VoucherStatus.PURCHASED.equals(voucher.getStatus()) ||
            VoucherStatus.RESERVED.equals(voucher.getStatus()) ||
            VoucherStatus.REDEEMED.equals(voucher.getStatus()))) {
            throw new BadRequestException(Constants.WRONG_STATUS_ERROR, String.format("Voucher with code %s is not Billed", code));
        }

        return Optional.of(voucher)
            .map(v -> {
                Vouchers vouchers = voucherMapper.toDto(v);
                vouchers.setTypeId(typeId);

                return vouchers;
            });
    }
    
    public CDR createCdr(Voucher voucher, VoucherType voucherType, String contractId) {
    	CDR cdrtest = null;
        try {
				cdrtest = new CDR.Builder()
						.withContractId(converContract(contractId))
						.withInstanceId(0l)
						.withCdrClass(Constants.CDR_CLASS)
						.withCdrType(Constants.CDR_TYPE)
						.withOrderEventTimestamp(convertDate(voucher.getTransactionDate()))
						.withServiceEventTimestamp(convertDate(voucher.getPurchaseDate()))
						.withCdrTimestamp(Date.from(clock.instant()))
						.withServiceId(Constants.CDR_SERVICE_ID)
						.withTariffClass(1l)
						.withChargingAmount(Constants.CDR_CHARGING_AMOUNT)
						.withCostCenter(voucherType.getShopId())
						.withOriginAddress(voucher.getCode())
						.withOriginProtocol(voucherType.getPromo())
						.withOriginId(voucherType.getProduct())
						.withSenderId(Constants.CDR_SENDER_ID)
						.withDeliveryStatus(1)
						.withPrice(convertAmount(voucherType.getAmount()))
						.withIsPriceGross(true)
						.withCurrency(voucherType.getCurrency())
						.withUniqueMessageId(voucherType.getPaymentProvider() + "_" + voucher.getTransactionId())
						.withSessionId(voucher.getCode()) 
						.withDestination(voucher.getUserId())
						.withDeliveryElement(voucherType.getPaymentProvider())
						.withMachineId(voucher.getTransactionId())
						.withCdrInfo1(Constants.CDR_INFO_1)
						.withCdrInfo2(Constants.CDR_INFOR_2)
						.withCdrInfo3(Constants.CDR_INFO_3)
						.withCountryId(voucherType.getCountry())
						//.withAdditionalInfo(null)
						//.withDistanceTable(null)
				        .build();
			} catch (CDRValidationException e) {
				log.error("exception trying to generate CDR notification of vocuher: {} with voucherType: {}", voucher.getCode(), voucherType.getCode());
			}
        return cdrtest;
    }

	private Long converContract(String contractId) {
		Long result = 0l;
		if(!Strings.isNullOrEmpty(contractId)) {
			try {
				result = Long.parseLong(contractId);							
			} catch (Exception e) {
				log.debug("Error trying to parse contractId {}", contractId);
			}
		}
		return result;
	}

	private Date convertDate(LocalDateTime date) {
		Date result = null;
		try {
		 result = Date.from(date.atZone(clock.getZone()).toInstant());
		 } catch (Exception e) {
			 log.debug("Error trying to convert to date {}", date);
		}
		return result;
	}

	private Integer convertAmount(BigDecimal voucherAmount){
		BigDecimal result = voucherAmount.multiply(Constants.CDR_P_FACTOR);
		return result.intValue();
	}
}
