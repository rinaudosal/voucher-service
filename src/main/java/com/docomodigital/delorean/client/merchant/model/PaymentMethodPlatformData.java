package com.docomodigital.delorean.client.merchant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodPlatformData {
	private String mode;
	private String docomoMerchantId;
	private String apiKey;
}
