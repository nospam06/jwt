package org.example.jwt.jpa.converter;

import org.example.jwt.dto.UserDto;
import org.example.jwt.jpa.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoUserConverter implements Converter<UserDto, User> {
    @Override
    public User convert(UserDto source) {
        User user = new User();
        BeanUtils.copyProperties(source, user);
        return user;
    }
}
