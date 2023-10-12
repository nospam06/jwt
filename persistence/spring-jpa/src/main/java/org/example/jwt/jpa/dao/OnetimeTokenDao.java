package org.example.jwt.jpa.dao;

import lombok.RequiredArgsConstructor;
import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.jpa.PersistenceException;
import org.example.jwt.jpa.api.JdbcDao;
import org.example.jwt.jpa.model.OnetimeToken;
import org.example.jwt.jpa.repository.OnetimeTokenRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OnetimeTokenDao implements JdbcDao<OnetimeTokenDto> {
    private final ConversionService conversionService;
    private final OnetimeTokenRepository repository;

    @Override
    public OnetimeTokenDto insert(OnetimeTokenDto dto) {
        OnetimeToken onetimeToken = conversionService.convert(dto, OnetimeToken.class);
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(TimeUnit.HOURS.toSeconds(1));
        String uuid = UUID.randomUUID().toString();
        onetimeToken.setToken(uuid);
        onetimeToken.setCreateDate(now);
        onetimeToken.setExpirationDate(expiration);
        repository.save(onetimeToken);
        return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot insert onetime token " + uuid, null));
    }

    @Override
    public OnetimeTokenDto update(OnetimeTokenDto dto) {
        String uuid = dto.getToken();
        repository.findById(uuid).ifPresent(onetimeToken -> {
            BeanUtils.copyProperties(dto, onetimeToken, "uuid", "createDate");
            repository.save(onetimeToken);
        });
        return findOne(uuid).orElseThrow(() -> new PersistenceException("cannot update onetime token " + uuid, null));
    }

    @Override
    public void delete(OnetimeTokenDto OnetimeTokenDto) {
        repository.deleteById(OnetimeTokenDto.getToken());
    }

    @Override
    public Optional<OnetimeTokenDto> findOne(String uuid) {
        return repository.findById(uuid)
                .map(onetimeToken -> conversionService.convert(onetimeToken, OnetimeTokenDto.class));

    }
}
