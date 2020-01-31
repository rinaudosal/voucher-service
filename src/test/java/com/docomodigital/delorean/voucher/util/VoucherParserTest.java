package com.docomodigital.delorean.voucher.util;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.UUID;

/**
 * 2020/01/30
 *
 * @author salvatore.rinaudo@docomodigital.com
 */
@Ignore
public class VoucherParserTest {

    private File voucherFile;

    @Before
    public void setUp() {
        voucherFile = new File("src/test/resources/Docomo_TEST_Set1_1MonthPlus1186294897430383169.csv");
        if (!voucherFile.exists()) {
            Assert.fail();
        }
    }

    @Test
    public void readCsvFile() throws Exception {
        FileReader fileReader = new FileReader("src/test/resources/Docomo_TEST_Set1_1MonthPlus1186294897430383169.csv");
        System.out.println(fileReader.getEncoding());
        BufferedReader csvReader = new BufferedReader(fileReader);
        String row;
        while ((row = csvReader.readLine()) != null) {
            Assertions.assertThat(UUID.fromString(row)).isNotNull();
            // do something with the data
        }
        csvReader.close();

        Scanner sc = new Scanner(System.in);

        String str = sc.nextLine();
    }


}
