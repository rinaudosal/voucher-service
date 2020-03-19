package com.docomodigital.delorean.client.merchant;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;

import feign.Logger;
import feign.Request;

@EnableConfigurationProperties
@Validated
@ConfigurationProperties(prefix = "merchant-client")
public class MerchantClientConfiguration {

	@Autowired ApplicationContext ctx;
	
	@NotNull
	Integer connectionTimeout;
	@NotNull
	Integer readTimeout;
	
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}	

	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	public Request.Options options() {
		return new Request.Options(connectionTimeout, readTimeout);
	}
}