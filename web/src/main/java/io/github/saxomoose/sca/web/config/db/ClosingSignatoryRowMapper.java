package io.github.saxomoose.sca.web.config.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;
import io.github.saxomoose.sca.web.models.ClosingSignatory;

public class ClosingSignatoryRowMapper implements RowMapper<ClosingSignatory> {

    @Override
    public ClosingSignatory mapRow(ResultSet rs, int rowNum) throws SQLException 
    {
        Timestamp timestamp = rs.getTimestamp("signature_timestamp");
        LocalDateTime signatureTimestamp = null;
        if (timestamp != null) {
            signatureTimestamp = timestamp.toLocalDateTime();
        }
        return new ClosingSignatory(
            rs.getLong("closing"),
            rs.getLong("signatory"),
            rs.getLong("document"),
            signatureTimestamp);
    }
    
}
