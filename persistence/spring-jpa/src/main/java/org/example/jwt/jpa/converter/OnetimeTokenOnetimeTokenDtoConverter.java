package org.example.jwt.jpa.converter;

import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.jpa.model.OnetimeToken;
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
