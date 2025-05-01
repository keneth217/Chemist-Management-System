package com.chemist.system.config.tenants;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.chemist.system.repository.central",
        entityManagerFactoryRef = "centralEntityManagerFactory",
        transactionManagerRef = "centralTransactionManager"
)
public class CentralDatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.central")
    public DataSource centralDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "centralEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean centralEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("centralDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.chemist.system.models")
                .persistenceUnit("central1")
                .build();
    }

    @Bean(name = "centralTransactionManager")
    public PlatformTransactionManager centralTransactionManager(
            @Qualifier("centralEntityManagerFactory") EntityManagerFactory centralEntityManagerFactory) {
        return new JpaTransactionManager(centralEntityManagerFactory);
    }
}