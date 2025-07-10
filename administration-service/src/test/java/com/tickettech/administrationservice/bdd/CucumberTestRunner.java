package com.tickettech.administrationservice.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
  features = "src/test/resources/features",
  glue = "com.tickettech.administrationservice.bdd",
  plugin = {"pretty", "html:target/cucumber-report.html"},
  monochrome = true
)
public class CucumberTestRunner {}
