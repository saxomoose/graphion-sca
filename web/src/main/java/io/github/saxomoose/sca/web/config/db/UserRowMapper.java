package io.github.saxomoose.sca.web.config.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import io.github.saxomoose.sca.web.models.User;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException 
    {
        return new User(
            rs.getLong("id"),
            rs.getBoolean("enabled"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("username"),
            rs.getDate("date_of_birth").toLocalDate(),
            rs.getBoolean("electronic_signature_consent"));
    }
}
