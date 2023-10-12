package org.example.jwt.jpa.converter;

import org.example.jwt.dto.UserTokenDto;
import org.example.jwt.jpa.model.UserToken;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserTokenUserTokenDtoConverter implements Converter<UserToken, UserTokenDto> {
    @Override
    public UserTokenDto convert(UserToken source) {
        UserTokenDto onetimeToken = new UserTokenDto();
        BeanUtils.copyProperties(source, onetimeToken);
        return onetimeToken;
    }
}
