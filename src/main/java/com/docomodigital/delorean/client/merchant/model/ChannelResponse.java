package com.docomodigital.delorean.client.merchant.model;

import com.docomodigital.delorean.domain.resource.Shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelResponse {
	private String id;
	private String status;
	private String merchantId;
	private Shop shop;
}
