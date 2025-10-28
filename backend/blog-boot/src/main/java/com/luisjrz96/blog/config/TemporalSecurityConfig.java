package com.luisjrz96.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.DummyUserProvider;

@Configuration
public class TemporalSecurityConfig {

  @Bean
  public UserProvider userProvider() {
    return new DummyUserProvider();
  }
}
