package com.luisjrz96.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.luisjrz96.blog.adapters.security.bridge.KeycloakUserContextProvider;
import com.luisjrz96.blog.application.shared.port.UserProvider;

@Configuration
public class UserProviderConfig {

  @Bean
  public UserProvider userProvider() {
    return new KeycloakUserContextProvider();
  }
}
