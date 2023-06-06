package io.github.saxomoose.sca.web.config.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import io.github.saxomoose.sca.web.models.Tenant;

public class TenantRowMapper implements RowMapper<Tenant> {
    @Override
    public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException 
    {
        return new Tenant(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("bucket"));
    }
    
}
