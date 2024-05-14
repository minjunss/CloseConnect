package com.CloseConnect.closeconnect.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class QueryDslTestConfig {
    @PersistenceContext
    private EntityManager testEntityManager;

    @Bean
    public JPAQueryFactory testJpaQueryFactory(){
        return new JPAQueryFactory(testEntityManager);
    }
}
