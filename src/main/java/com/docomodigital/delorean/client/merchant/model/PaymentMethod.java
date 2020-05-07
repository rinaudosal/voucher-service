package com.docomodigital.delorean.client.merchant.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {
	private String id;
	private boolean enabled;
	private String brand;
	private Map<String, String> brandData;
	private String platform;
	private PaymentMethodPlatformData platformData;
	private Map<String, ActionParamsDescriptor> descriptor;
}
