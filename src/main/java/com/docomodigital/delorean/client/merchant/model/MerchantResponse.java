package com.docomodigital.delorean.client.merchant.model;

import java.util.List;
import java.util.Optional;

import com.docomodigital.delorean.domain.resource.Shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponse {
	List<Shop> shops;
	
	public Optional<Shop> findShopById(String shopId) {
		return shops.stream().filter(e -> e.getId().equals(shopId)).findFirst();
	}
}
