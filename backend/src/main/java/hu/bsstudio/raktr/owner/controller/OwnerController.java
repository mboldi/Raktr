package hu.bsstudio.raktr.owner.controller;

import hu.bsstudio.raktr.dto.owner.OwnerCreateDto;
import hu.bsstudio.raktr.dto.owner.OwnerDetailsDto;
import hu.bsstudio.raktr.dto.owner.OwnerUpdateDto;
import hu.bsstudio.raktr.owner.service.OwnerService;
import hu.bsstudio.raktr.security.RoleConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Owners")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owner")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public List<OwnerDetailsDto> listOwners() {
        return ownerService.listOwners();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerDetailsDto createOwner(@RequestBody @Valid OwnerCreateDto createDto) {
        return ownerService.createOwner(createDto);
    }

    @Secured(RoleConstants.MEMBER)
    @PutMapping("/{ownerId}")
    public OwnerDetailsDto updateOwner(@PathVariable Long ownerId, @RequestBody @Valid OwnerUpdateDto updateDto) {
        return ownerService.updateOwner(ownerId, updateDto);
    }

    @DeleteMapping("/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwner(@PathVariable Long ownerId) {
        ownerService.deleteOwner(ownerId);
    }

}
