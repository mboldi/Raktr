package hu.bsstudio.raktr.config.service;

import hu.bsstudio.raktr.config.mapper.ConfigMapper;
import hu.bsstudio.raktr.dal.entity.Config;
import hu.bsstudio.raktr.dal.repository.ConfigRepository;
import hu.bsstudio.raktr.dal.value.ConfigKey;
import hu.bsstudio.raktr.dto.appconfig.ConfigDetailsDto;
import hu.bsstudio.raktr.dto.appconfig.ConfigUpdateDto;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    private final ConfigMapper configMapper;

    @Cacheable(value = "configs")
    public List<ConfigDetailsDto> listConfigs() {
        var configs = configRepository.findAll();
        return configs.stream().map(configMapper::entityToDetailsDto).toList();
    }

    @CacheEvict(value = "configs", allEntries = true)
    @Transactional
    public ConfigDetailsDto updateConfig(String configKey, ConfigUpdateDto updateDto) {
        var key = parseConfigKey(configKey);
        var config = getConfig(key);

        config.getDataType().validate(updateDto.getValue());

        config.setValue(updateDto.getValue());
        config = configRepository.saveAndFlush(config);

        log.info("Updated Config [{}] to [{}]", key, updateDto.getValue());

        return configMapper.entityToDetailsDto(config);
    }

    @Cacheable(value = "configs", key = "'str:' + #key.name()")
    public String getString(ConfigKey key) {
        return getConfig(key).getValue();
    }

    private Config getConfig(ConfigKey key) {
        return configRepository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException(Config.class, key));
    }

    private ConfigKey parseConfigKey(String key) {
        try {
            return ConfigKey.valueOf(key);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(Config.class, key);
        }
    }

}
