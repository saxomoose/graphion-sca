package io.github.saxomoose.sca.web.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TenantRowMapper implements RowMapper<Tenant> {
    @Override
    public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Tenant(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("bucket"));
    }
    
}
