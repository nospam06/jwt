package org.example.jwt.jpa.converter;

import org.example.jwt.dto.UserDto;
import org.example.jwt.jpa.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserUserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User source) {
        UserDto user = new UserDto();
        BeanUtils.copyProperties(source, user);
        return user;
    }
}
