package com.javadeveloperzone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableMongoRepositories("com.javadeveloperzone")
public class MongoConfiguration {
  @Bean
  MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }
  @Bean
  public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDbFactory,
                                                     MongoMappingContext context, MongoCustomConversions conversions) {
    MappingMongoConverter mappingConverter = new MappingMongoConverter(mongoDbFactory, context);
    mappingConverter.setCustomConversions(conversions);
    mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
    return mappingConverter;
  }

}
