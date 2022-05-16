package org.example.jwt.jdbc.converter;

import org.example.jwt.model.UserToken;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class UserTokenRowMapper implements RowMapper<UserToken> {
    @Override
    public UserToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserToken userToken = new UserToken();
        userToken.setUuid(rs.getString("uuid"));
        userToken.setUserUuid(rs.getString("user_uuid"));
        Timestamp createDate = rs.getTimestamp("create_date");
        Instant createInstant = createDate.toInstant();
        userToken.setCreateDate(createInstant);
        Timestamp expirationDate = rs.getTimestamp("expiration_date");
        Instant expirationInstant = expirationDate.toInstant();
        userToken.setExpirationDate(expirationInstant);
        return userToken;
    }
}
