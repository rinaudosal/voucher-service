package com.docomodigital.delorean.voucher.util;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 2020/01/30
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
public class VoucherFileGenerator {

    public static void main(String[] args) throws Exception {
        Writer fstream = new OutputStreamWriter(new FileOutputStream("DOCOMO_6_1000.csv"), StandardCharsets.UTF_8);

//                fstream.append(code);
//                fstream.append("\n");

        for (int i = 0; i < 1000; i++) {
            fstream.append(UUID.randomUUID().toString());
            fstream.append("\n");
        }

        fstream.flush();
        fstream.close();
    }

}
