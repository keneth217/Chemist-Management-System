package com.chemist.system.config.tenants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Autowired
    TenantDataSourceProvider provider;

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant() == null ? "public" : TenantContext.getCurrentTenant();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        return provider.getDataSource(TenantContext.getCurrentTenant());
    }
}

