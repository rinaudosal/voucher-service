package com.docomodigital.delorean.voucher.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"json:target/cucumber.json"}, features = "src/test/features")
public class CucumberIT {

}
