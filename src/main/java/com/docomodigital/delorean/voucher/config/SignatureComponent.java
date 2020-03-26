package com.docomodigital.delorean.voucher.config;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 2020/03/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Slf4j
@Component
public class SignatureComponent {

    public boolean validateSignature(String privateKey, String signatureKey, byte[] body) {

        log.info(String.format("PrivateKey of the shop: %s", privateKey));
        log.info(String.format("SignatureKey header: %s", signatureKey));

        String bodyEncoded = Base64.getEncoder()
            .encodeToString(body);
        log.info(String.format("Body: %s", new String(body)));
        log.info(String.format("BodyEncoded: %s", signatureKey));

        String content = String.format("%s.%s",
            privateKey,
            bodyEncoded);

        final String hash = Hashing.sha256()
            .hashString(content, StandardCharsets.UTF_8)
            .toString();

        log.info(String.format("hash encoded data: %s", hash));
        return hash.equals(signatureKey);
    }
}
