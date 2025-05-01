package com.chemist.system.config.tenants;

import com.chemist.system.models.Chemist;
import com.chemist.system.services.ChemistService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantDataSourceProvider {

    private final ChemistService chemistService;
    private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();
    private final DataSource defaultDataSource;
    private final String tenantUrlPattern;
    private final String username;
    private final String password;

    @Autowired
    public TenantDataSourceProvider(
            @Lazy ChemistService chemistService,
            @Value("${spring.tenant.datasource.url-pattern}") String tenantUrlPattern,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        this.chemistService = chemistService;
        this.tenantUrlPattern = tenantUrlPattern;
        this.username = username;
        this.password = password;
        this.defaultDataSource = createDefaultDataSource();
        initializeTenantDataSources();
    }

    private void initializeTenantDataSources() {
        tenantDataSources.put("public", defaultDataSource);
        chemistService.getAllChemists().forEach(chemist ->
                tenantDataSources.put(chemist.getChemistId(), createTenantDataSource(chemist))
        );
    }

    public DataSource getDataSource(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            return defaultDataSource;
        }
        return (DataSource) tenantDataSources.computeIfAbsent(tenantId,
                id -> createTenantDataSource(chemistService.findByChemistId((String) id))
        );
    }

    private DataSource createDefaultDataSource() {
        HikariDataSource ds = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url("jdbc:mysql://localhost/chemist_central?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .username(username)
                .password(password)
                .build();
        configureHikariPool(ds, "CentralHikariPool");
        return ds;
    }

    private DataSource createTenantDataSource(Chemist chemist) {
        if (chemist == null) {
            return defaultDataSource;
        }
        String jdbcUrl = tenantUrlPattern.replace("#{tenantId}", String.valueOf(chemist.getChemistId()));

        HikariDataSource ds = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(jdbcUrl)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .username(username)
                .password(password)
                .build();

        configureHikariPool(ds, "TenantHikariPool-" + chemist.getChemistId());
        return ds;
    }

    private void configureHikariPool(HikariDataSource dataSource, String poolName) {
        dataSource.setPoolName(poolName);
        dataSource.setMinimumIdle(2);
        dataSource.setMaximumPoolSize(5);
        dataSource.setIdleTimeout(60000);
        dataSource.setMaxLifetime(300000);
        dataSource.setConnectionTimeout(10000);
        dataSource.setLeakDetectionThreshold(15000);
        dataSource.setKeepaliveTime(30000);
    }

    public Map<Object, Object> getAllDataSources() {
        return new ConcurrentHashMap<>(tenantDataSources);
    }

    public void createDataSourceForTenant(Chemist chemist) {
        if (chemist != null) {
            DataSource dataSource = createTenantDataSource(chemist);
            tenantDataSources.put(chemist.getChemistId(), dataSource);
        }
    }
}