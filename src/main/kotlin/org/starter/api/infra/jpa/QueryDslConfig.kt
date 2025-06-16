package org.starter.api.infra.jpa

import com.blazebit.persistence.querydsl.JPQLNextTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import com.querydsl.sql.H2Templates
import com.querydsl.sql.MySQLTemplates
import com.querydsl.sql.SQLTemplates
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryDslConfig {
    @PersistenceContext
    private val entityManager: EntityManager? = null

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(JPQLNextTemplates.DEFAULT, entityManager)
    }

//    @Bean
//    fun mysqlTemplates(): SQLTemplates {
//        return MySQLTemplates.builder().build()
//    }

    fun h2SqlTemplates(): SQLTemplates {
        return H2Templates.builder().build()
    }
}
