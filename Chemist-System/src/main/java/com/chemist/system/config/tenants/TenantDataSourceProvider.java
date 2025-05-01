package com.chemist.system.config.tenants;

import com.chemist.system.models.Chemist;
import com.chemist.system.services.ChemistService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Lazy;
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

    public TenantDataSourceProvider(
            @Lazy ChemistService chemistService,
            @Value("${spring.tenant.datasource.url-pattern}") String tenantUrlPattern,
            @Value("${spring.datasource.central.username}") String username,
            @Value("${spring.datasource.central.password}") String password) {
        this.chemistService = chemistService;
        this.tenantUrlPattern = tenantUrlPattern;
        this.username = username;
        this.password = password;
        this.defaultDataSource = createDefaultDataSource();
    }

    public DataSource getDataSource(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            return defaultDataSource;
        }
        return (DataSource) tenantDataSources.computeIfAbsent(tenantId,
                id -> createTenantDataSource(chemistService.findByChemistId((String) id)));
    }

    private DataSource createDefaultDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url("jdbc:mysql://localhost/chemist_central?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC")
                .username(username)
                .password(password)
                .build();
    }

    private DataSource createTenantDataSource(Chemist chemist) {
        if (chemist == null) {
            return defaultDataSource;
        }
        String jdbcUrl = tenantUrlPattern.replace("%s", String.valueOf(chemist.getChemistId()));
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .build();
    }

    public Map<Object, Object> getAllDataSources() {
        return tenantDataSources;
    }

    public DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    public void createDataSourceForTenant(Chemist chemist) {
        if (chemist != null) {
            DataSource dataSource = createTenantDataSource(chemist);
            tenantDataSources.put(chemist.getChemistId(), dataSource);
        }
    }
}