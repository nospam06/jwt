package org.example.jwt.jdbc.converter;

import org.example.jwt.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUuid(rs.getString("uuid"));
        user.setPasswordSha(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        Timestamp createDate = rs.getTimestamp("create_date");
        Instant createInstant = createDate.toInstant();
        user.setCreateDate(createInstant);
        Timestamp updateDate = rs.getTimestamp("update_date");
        Instant updateInstant = updateDate.toInstant();
        user.setUpdateDate(updateInstant);
        return user;
    }
}
