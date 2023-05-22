package io.github.saxomoose.sca.web.services;

import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.config.db.RowMapperCollection;
import io.github.saxomoose.sca.web.enums.Function;
import io.github.saxomoose.sca.web.models.LegalPerson;

@Service
public class LegalPersonService {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RowMapperCollection rowMappers;

    public LegalPersonService(NamedParameterJdbcOperations namedParameterJdbcOperations, RowMapperCollection rowMappers) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.rowMappers = rowMappers;
    }

    // Read

    public LegalPerson firstLegalPerson() {
        final var sql = """
        SELECT * FROM sca.legal_person LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, rowMappers.getRowMapper(LegalPerson.class));
    }

    // Update
    public LegalPerson insert(LegalPerson legalPerson, Long principalId)
    {
        final var sql = """
        INSERT INTO sca.legal_person
        VALUES (:tenantId, DEFAULT, :enterpriseNumber, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
        RETURNING *;        
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "tenantId", legalPerson.tenantId(),
                "enterpriseNumber", legalPerson.tenantId(),
                "createdBy", principalId,
                "lastUpdatedBy", principalId));
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(LegalPerson.class));
    }

    public void upsertOfficer(Long legalPersonId, Long signatoryId, Function function) {
        upsertOfficer(legalPersonId, signatoryId, function, null);
    }

    public void upsertOfficer(Long legalPersonId, Long signatoryId, Function function, Long enterpriseNumber) 
    {
        final var sql = """
            INSERT INTO sca.officer (legal_person, signatory, "function", enterprise_number)
            VALUES (:legalPersonId, :signatoryId, :function, :enterpriseNumber)
            ON CONFLICT (legal_person, signatory, "function") DO UPDATE
            SET "function" = :function, enterprise_number = :enterpriseNumber
        """;
        final var params = new MapSqlParameterSource().addValues(
            Map.of(
                "legalPersonId", legalPersonId,
                "signatoryId", signatoryId,
                "function", function.toString()));
        params.addValue("enterpriseNumber", enterpriseNumber, java.sql.Types.BIGINT);

        namedParameterJdbcOperations.update(sql, params);
    }
}
