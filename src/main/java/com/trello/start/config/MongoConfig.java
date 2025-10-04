package com.trello.start.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;

@Configuration
public class MongoConfig {

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        String uri = System.getenv("MONGO_URI");
        ReactiveMongoDatabaseFactory factory = new SimpleReactiveMongoDatabaseFactory(MongoClients.create(uri), "trellodb");
        return new ReactiveMongoTemplate(factory);
    }
}
