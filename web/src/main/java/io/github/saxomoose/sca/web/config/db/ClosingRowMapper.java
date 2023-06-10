package io.github.saxomoose.sca.web.config.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;
import io.github.saxomoose.sca.web.models.Closing;

public class ClosingRowMapper implements RowMapper<Closing> {

    @Override
    public Closing mapRow(ResultSet rs, int rowNum) throws SQLException 
    {
        return new Closing(
            rs.getLong("tenant"),
            rs.getLong("id"),
            rs.getString("name"),
            rs.getTimestamp("timestamp").toLocalDateTime(),
            ZoneId.of(rs.getString("zone_id")),
            rs.getString("location"),
            rs.getTimestamp("expiration_timestamp").toLocalDateTime(),
            rs.getBoolean("invites_sent"));
    }
    
}
