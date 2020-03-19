package com.docomodigital.delorean.client.merchant;

import com.docomodigital.delorean.client.merchant.model.ChannelResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "merchantClient",
    configuration = MerchantClientConfiguration.class,
    url = "${merchant-client.url}")
public interface MerchantClient {

    @GetMapping(value = "/channel")
    ChannelResponse getChannelByApiKey(@RequestParam("apiKey") String apiKey);

}
