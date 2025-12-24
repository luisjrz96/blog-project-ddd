package com.luisjrz96.blog.adapters.persistence.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

  @Bean(initMethod = "migrate")
  public Flyway flyway(DataSource dataSource) {
    return Flyway.configure()
        .baselineOnMigrate(true)
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load();
  }

  @Bean
  public FlywayMigrationStrategy flywayMigrationStrategy() {
    return Flyway::migrate;
  }
}
