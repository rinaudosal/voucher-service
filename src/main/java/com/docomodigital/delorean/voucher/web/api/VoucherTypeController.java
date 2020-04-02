package com.docomodigital.delorean.voucher.web.api;

import com.docomodigital.delorean.voucher.domain.VoucherType;
import com.docomodigital.delorean.voucher.service.VoucherTypeService;
import com.docomodigital.delorean.voucher.web.api.model.AvailableVoucherTypes;
import com.docomodigital.delorean.voucher.web.api.model.VoucherTypes;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of voucher-type endpoint to manage the voucher types
 * 2020/01/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Controller
@Api(value = "voucher-type", tags = {"Voucher types configuration"})
@RequestMapping("/v1")
public class VoucherTypeController implements VoucherTypeApi {

    private final VoucherTypeService voucherTypeService;

    public VoucherTypeController(VoucherTypeService voucherTypeService) {
        this.voucherTypeService = voucherTypeService;
    }

    @Override
    public ResponseEntity<List<AvailableVoucherTypes>> getAvailableVoucherTypes(String merchant, String paymentProvider, String country) {

        List<AvailableVoucherTypes> availableVoucherTypes = voucherTypeService.getAvailableVoucherTypes(merchant, paymentProvider, country);
        if (availableVoucherTypes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(availableVoucherTypes);
    }

    @Override
    public ResponseEntity<List<VoucherTypes>> getVoucherTypes(@Valid String merchant,
                                                              @Valid String country,
                                                              @Valid String paymentProvider,
                                                              @Valid String currency,
                                                              @Valid String shop,
                                                              @Valid Boolean enabled) {
        VoucherType voucherType = new VoucherType();
        voucherType.setCurrency(currency);
        voucherType.setMerchantId(merchant);
        voucherType.setPaymentProvider(paymentProvider);
        voucherType.setCountry(country);
        voucherType.setShopId(shop);
        voucherType.setEnabled(enabled);

        voucherType.setCreatedDate(null);
        voucherType.setLastModifiedDate(null);
        Example<VoucherType> example = Example.of(voucherType);
        List<VoucherTypes> voucherTypes = voucherTypeService.getVoucherTypes(example);

        return ResponseEntity.ok(voucherTypes);
    }

    @Override
    public ResponseEntity<VoucherTypes> getVoucherType(String code) {
        return ResponseEntity.of(voucherTypeService.getVoucherType(code));
    }

    @Override
    public ResponseEntity<VoucherTypes> createVoucherType(@Valid VoucherTypes voucherTypes) {
        VoucherTypes voucherType = voucherTypeService.createVoucherType(voucherTypes);

        return ResponseEntity.created(URI.create("/v1/voucher-type/" + voucherType.getTypeId()))
            .body(voucherType);
    }

    @Override
    public ResponseEntity<VoucherTypes> updateVoucherType(String code, @Valid VoucherTypes voucherTypes) {
        Optional<VoucherTypes> voucherType = voucherTypeService.updateVoucherType(code, voucherTypes);

        return ResponseEntity.of(voucherType);
    }

}
