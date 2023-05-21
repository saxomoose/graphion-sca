package io.github.saxomoose.sca.web.services;

import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.enums.Function;
import io.github.saxomoose.sca.web.services.repositories.LegalPersonRepository;

@Service
public class LegalPersonService {
    private final LegalPersonRepository legalPersonRepository;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public LegalPersonService(LegalPersonRepository legalPersonRepository, NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.legalPersonRepository = legalPersonRepository;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    public void upsertOfficer(Long legalPersonId, Long signatoryId, Function function) {
        upsertOfficer(legalPersonId, signatoryId, function, null);
    }

    public void upsertOfficer(Long legalPersonId, Long signatoryId, Function function, Long enterpriseNumber) {
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
