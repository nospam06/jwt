package org.example.jwt.jdbc.converter;

import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.dto.UserDto;
import org.example.jwt.model.OnetimeToken;
import org.example.jwt.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OnetimeTokenOnetimeTokenDtoConverter implements Converter<OnetimeToken, OnetimeTokenDto> {
    @Override
    public OnetimeTokenDto convert(OnetimeToken source) {
        OnetimeTokenDto dto = new OnetimeTokenDto();
        BeanUtils.copyProperties(source, dto);
        return dto;
    }
}
