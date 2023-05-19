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
            rs.getString("bucket"),
            rs.getLong("created_by"),
            rs.getLong("last_modified_by"),
            rs.getTimestamp("created_date").toLocalDateTime(),
            rs.getTimestamp("last_modified_date").toLocalDateTime());
    }
    
}
