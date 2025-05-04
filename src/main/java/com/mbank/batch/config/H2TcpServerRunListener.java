package com.mbank.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.SQLException;
import java.util.Arrays;

@Slf4j
public class H2TcpServerRunListener implements SpringApplicationRunListener {

  private final SpringApplication application;

  public H2TcpServerRunListener(SpringApplication application, String[] args) {
    this.application = application;
  }

  @Override
  public void starting(ConfigurableBootstrapContext bootstrapContext) {
  }

  @Override
  public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
    ConfigurableEnvironment environment) {
    String[] activeProfiles = environment.getActiveProfiles();
    boolean isDev = Arrays.asList(activeProfiles).contains("dev");

    if (isDev) {
      try {
        Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
        log.info("Starting H2 TCP server with Dev profile");
      } catch (SQLException e) {
        throw new RuntimeException("Failed to start H2 TCP server", e);
      }
    } else {
      log.info("Skipping H2 TCP server startup (profile: " + Arrays.toString(activeProfiles) + ")");
    }
  }
}
