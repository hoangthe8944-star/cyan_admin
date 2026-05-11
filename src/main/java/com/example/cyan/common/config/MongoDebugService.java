package com.example.cyan.common.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.example.cyan.admin.dto.MongoDebugInfoResponse;
import com.mongodb.ConnectionString;

@Service
public class MongoDebugService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoDebugService.class);

    private final MongoTemplate mongoTemplate;
    private final String configuredMongoUri;

    public MongoDebugService(MongoTemplate mongoTemplate,
            @Value("${spring.mongodb.uri:${spring.data.mongodb.uri:}}") String configuredMongoUri) {
        this.mongoTemplate = mongoTemplate;
        this.configuredMongoUri = configuredMongoUri;
    }

    public MongoDebugInfoResponse getInfo() {
        ConnectionString connectionString = new ConnectionString(configuredMongoUri);
        List<String> hosts = connectionString.getHosts();
        String database = mongoTemplate.getDb().getName();
        String scheme = configuredMongoUri.contains("://")
                ? configuredMongoUri.substring(0, configuredMongoUri.indexOf("://"))
                : "unknown";

        return new MongoDebugInfoResponse(database, hosts, scheme, maskUri(configuredMongoUri));
    }

    @Override
    public void run(ApplicationArguments args) {
        MongoDebugInfoResponse info = getInfo();
        log.info("Mongo debug info -> database: {}, hosts: {}, scheme: {}, uri: {}",
                info.database(), info.hosts(), info.scheme(), info.configuredUriMasked());
    }

    private String maskUri(String uri) {
        int schemeIndex = uri.indexOf("://");
        if (schemeIndex < 0) {
            return uri;
        }

        int atIndex = uri.indexOf('@', schemeIndex + 3);
        if (atIndex < 0) {
            return uri;
        }

        return uri.substring(0, schemeIndex + 3) + "***:***" + uri.substring(atIndex);
    }
}
