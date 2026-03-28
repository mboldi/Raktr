package hu.bsstudio.raktr.config.controller;

import hu.bsstudio.raktr.config.service.ConfigService;
import hu.bsstudio.raktr.dto.appconfig.ConfigDetailsDto;
import hu.bsstudio.raktr.dto.appconfig.ConfigUpdateDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Config")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/configs")
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    public List<ConfigDetailsDto> listConfigs() {
        return configService.listConfigs();
    }

    @PutMapping("/{configKey}")
    public ConfigDetailsDto updateConfig(
            @PathVariable String configKey,
            @RequestBody @Valid ConfigUpdateDto updateDto
    ) {
        return configService.updateConfig(configKey, updateDto);
    }

}
