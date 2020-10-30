package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.service.GeneralDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequestMapping("/api/generalData")
public class GeneralDataController {
    private final GeneralDataService generalDataService;

    public GeneralDataController(final GeneralDataService generalDataService) {
        this.generalDataService = generalDataService;
    }
}
