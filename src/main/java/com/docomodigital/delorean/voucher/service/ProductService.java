package com.docomodigital.delorean.voucher.service;

import java.util.List;

/**
 * 2020/02/24
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public interface ProductService {

    List<String> getAvailableProducts(List<String> products);
}
