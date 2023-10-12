package org.example.jwt.jpa.converter;

import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.jpa.model.OnetimeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OnetimeTokenDtoOnetimeTokenConverter implements Converter<OnetimeTokenDto, OnetimeToken> {
    @Override
    public OnetimeToken convert(OnetimeTokenDto source) {
        OnetimeToken onetimeToken = new OnetimeToken();
        BeanUtils.copyProperties(source, onetimeToken);
        return onetimeToken;
    }
}
