package org.example.jwt.jpa.converter;

import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.jpa.model.UserToken;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserTokenDtoUserTokenConverter implements Converter<UserTokenDto, UserToken> {
    @Override
    public UserToken convert(UserTokenDto source) {
        UserToken onetimeToken = new UserToken();
        BeanUtils.copyProperties(source, onetimeToken);
        return onetimeToken;
    }
}
