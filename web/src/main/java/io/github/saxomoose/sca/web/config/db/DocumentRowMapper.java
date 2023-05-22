package io.github.saxomoose.sca.web.config.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import io.github.saxomoose.sca.web.models.Document;

public class DocumentRowMapper implements RowMapper<Document> {

    @Override
    public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Document(
            rs.getLong("tenant"),
            rs.getLong("id"),
            rs.getString("filename"),
            UUID.fromString(rs.getString("file_uuid")));
    }
    
}
