package com.sparta.modulebatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MetaDBConfig {

    @Primary // DataSource를 불러오는 DB가 충돌하지 않도록 우선 순위를 지정
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-meta") // 어플리케이션의 설정 값을 자동으로 불러옴 prefix : 변수명을 통해
    public DataSource metaDBSource() {

        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    // DB에 대한 트랜잭션 매니저를 등록
    public PlatformTransactionManager metaTransactionManager() {

        return new DataSourceTransactionManager(metaDBSource());
    }
}
