package org.example.jwt.jpa.config;

import org.example.jwt.jpa.converter.OnetimeTokenDtoOnetimeTokenConverter;
import org.example.jwt.jpa.converter.OnetimeTokenOnetimeTokenDtoConverter;
import org.example.jwt.jpa.converter.UserDtoUserConverter;
import org.example.jwt.jpa.converter.UserTokenDtoUserTokenConverter;
import org.example.jwt.jpa.converter.UserTokenUserTokenDtoConverter;
import org.example.jwt.jpa.converter.UserUserDtoConverter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@Configuration
@ComponentScan("org.example")
@EntityScan("org.example.jwt.jpa.model")
@EnableJpaRepositories("org.example.jwt.jpa.repository")
public class TestJpaConfig {
    @Bean
    public ConversionService addConverter() {
        GenericConversionService conversionService = new GenericConversionService();
        conversionService.addConverter(new OnetimeTokenOnetimeTokenDtoConverter());
        conversionService.addConverter(new OnetimeTokenDtoOnetimeTokenConverter());
        conversionService.addConverter(new UserDtoUserConverter());
        conversionService.addConverter(new UserUserDtoConverter());
        conversionService.addConverter(new UserTokenDtoUserTokenConverter());
        conversionService.addConverter(new UserTokenUserTokenDtoConverter());
        return conversionService;
    }
}
