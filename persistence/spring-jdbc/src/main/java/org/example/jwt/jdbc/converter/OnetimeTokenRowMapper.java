package org.example.jwt.jdbc.converter;

import org.example.jwt.model.OnetimeToken;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class OnetimeTokenRowMapper implements RowMapper<OnetimeToken> {
    @Override
    public OnetimeToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        OnetimeToken onetimeToken = new OnetimeToken();
        onetimeToken.setToken(rs.getString("token"));
        onetimeToken.setEmail(rs.getString("email"));
        Timestamp createDate = rs.getTimestamp("create_date");
        Instant createInstant = createDate.toInstant();
        onetimeToken.setCreateDate(createInstant);
        Timestamp updateDate = rs.getTimestamp("expiration_date");
        Instant updateInstant = updateDate.toInstant();
        onetimeToken.setExpirationDate(updateInstant);
        Timestamp usedDate = rs.getTimestamp("used_date");
        Optional.ofNullable(usedDate).map(Timestamp::toInstant)
                        .ifPresent(onetimeToken::setUsedDate);
        return onetimeToken;
    }
}
