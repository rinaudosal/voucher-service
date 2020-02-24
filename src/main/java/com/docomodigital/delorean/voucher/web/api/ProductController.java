package com.docomodigital.delorean.voucher.web.api;

import com.docomodigital.delorean.voucher.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import java.util.List;

/**
 * 2020/02/24
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Controller
@RequestMapping("/v1")
public class ProductController implements ProductsApi {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<List<String>> getAvailableProducts(@Valid List<String> products) {
        return ResponseEntity.ok(productService.getAvailableProducts(products));
    }

}
