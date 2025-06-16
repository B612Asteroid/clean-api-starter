package org.starter.api.infra.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

/**
 * Data source config
 *
 * @constructor Create empty Data source config
 */
@Configuration
class DataSourceConfig {

    /**
     * JPA 트랜잭션 사용 > Mybatis와 JPA 간 트랜잭션 공유가 가능하도록 설정
     *
     * @param entityManagerFactory
     * @return
     */
    @Bean
    fun  transactionManager( entityManagerFactory: EntityManagerFactory): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory);
    }
}
