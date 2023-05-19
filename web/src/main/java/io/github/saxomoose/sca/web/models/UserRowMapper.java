package io.github.saxomoose.sca.web.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
            rs.getLong("id"),
            UUID.fromString(rs.getString("uuid")),
            rs.getBoolean("enabled"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("username"),
            rs.getDate("date_of_birth").toLocalDate(),
            rs.getBoolean("electronic_signature_consent"),
            rs.getLong("created_by"),
            rs.getLong("last_modified_by"),
            rs.getTimestamp("created_date").toLocalDateTime(),
            rs.getTimestamp("last_modified_date").toLocalDateTime());
    }
}
