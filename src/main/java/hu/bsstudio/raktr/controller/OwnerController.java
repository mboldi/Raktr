package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.model.Owner;
import hu.bsstudio.raktr.service.OwnerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequiredArgsConstructor
@RequestMapping("/api/owner")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public ResponseEntity<List<Owner>> ownerList() {
        log.info("Incoming request for all owners");
        return ResponseEntity
            .ok(ownerService.getAll());
    }
}
