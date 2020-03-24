package com.docomodigital.delorean.voucher.config;

import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 2020/03/23
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Component
public class SignatureComponent {

    public boolean validateSignature(String privateKey, String signatureKey, byte[] body) {

        String bodyEncoded = Base64.getEncoder()
            .encodeToString(body);

        String content = String.format("%s.%s",
            privateKey,
            bodyEncoded);

        final String hash = Hashing.sha256()
            .hashString(content, StandardCharsets.UTF_8)
            .toString();

        return hash.equals(signatureKey);
    }
}
