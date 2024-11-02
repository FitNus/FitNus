package com.sparta.modulebatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.sparta.modulecommon"},// DataDBConfig가 어떤 패키지에서 동작하게 할 것인지
        entityManagerFactoryRef = "dataEntityManager", // 작성한 dataEntityManager 명을 의미
        transactionManagerRef = "dataTransactionManager" // // 작성한 dataTransactionManager 명을 의미
) // JPA를 사용해서 특정한 패키지의 Entity에다가 값을 부여
public class DataDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-data") // 두번째 DB Source에 대해 prefix 값을 이용하여 값을 불러옴
    public DataSource dataDBSource() {

        return DataSourceBuilder.create().build(); // DB를 연결하는 값이 Bean으로 등록됨
    }

    @Bean
    // Entity들을 관리하는 매니저
    public LocalContainerEntityManagerFactoryBean dataEntityManager() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataDBSource());
        em.setPackagesToScan(new String[]{"com.sparta.modulecommon"}); // Entity들이 모여질 패키지를 등록
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        // 원래는 properties에 넣어야 하는 값들이지만 두 개의 DB를 사용하기 때문에 강제로 설정
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    // DB에 대한 트랜잭션 매니저를 등록
    public PlatformTransactionManager dataTransactionManager() {

        JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(dataEntityManager().getObject());

        return transactionManager;
    }
}
