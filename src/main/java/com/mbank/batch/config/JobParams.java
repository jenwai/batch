package com.mbank.batch.config;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class JobParams {
  @Value("#{jobParameters['csvPath']}")
  private String csvPath;

  public String getCsvPath() {
    return csvPath;
  }
}
