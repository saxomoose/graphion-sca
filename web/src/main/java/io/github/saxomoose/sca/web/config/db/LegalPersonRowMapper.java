package io.github.saxomoose.sca.web.config.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import io.github.saxomoose.sca.web.models.LegalPerson;

public class LegalPersonRowMapper implements RowMapper<LegalPerson> {
    @Override
    public LegalPerson mapRow(ResultSet rs, int rowNum) throws SQLException 
    {
        return new LegalPerson(
            rs.getLong("tenant"),
            rs.getLong("id"),
            rs.getLong("enterprise_number"));
    }
    
}
