package com.javadeveloperzone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.query.Query;

@Configuration
public class GeneralConfiguration {

    @Bean
    public Query query(){
        return new Query();
    }
}
