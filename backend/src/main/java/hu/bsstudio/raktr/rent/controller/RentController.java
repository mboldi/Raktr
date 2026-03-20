package hu.bsstudio.raktr.rent.controller;

import hu.bsstudio.raktr.dto.comment.CommentCreateDto;
import hu.bsstudio.raktr.dto.comment.CommentDetailsDto;
import hu.bsstudio.raktr.dto.rent.RentCreateDto;
import hu.bsstudio.raktr.dto.rent.RentDetailsDto;
import hu.bsstudio.raktr.dto.rent.RentPdfCreateDto;
import hu.bsstudio.raktr.dto.rent.RentUpdateDto;
import hu.bsstudio.raktr.dto.rent.RentValidationIssueDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemCreateDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemDetailsDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemUpdateDto;
import hu.bsstudio.raktr.rent.service.RentService;
import hu.bsstudio.raktr.security.RoleConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Rents")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rents")
public class RentController {

    private final RentService rentService;

    @GetMapping
    public List<RentDetailsDto> listRents(@RequestParam(required = false, defaultValue = "false") boolean deleted) {
        return rentService.listRents(deleted);
    }

    @Secured(RoleConstants.MEMBER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentDetailsDto createRent(@RequestBody @Valid RentCreateDto createDto) {
        return rentService.createRent(createDto);
    }

    @GetMapping("/{rentId}")
    public RentDetailsDto getRentById(@PathVariable Long rentId) {
        return rentService.getRentById(rentId);
    }

    @Secured(RoleConstants.MEMBER)
    @PutMapping("/{rentId}")
    public RentDetailsDto updateRent(@PathVariable Long rentId, @RequestBody @Valid RentUpdateDto updateDto) {
        return rentService.updateRent(rentId, updateDto);
    }

    @Secured(RoleConstants.MEMBER)
    @DeleteMapping("/{rentId}")
    public void deleteRent(@PathVariable Long rentId) {
        rentService.deleteRent(rentId);
    }

    @Secured(RoleConstants.ADMIN)
    @PostMapping("/{rentId}/restore")
    public void restoreRent(@PathVariable Long rentId) {
        rentService.restoreRent(rentId);
    }

    @PostMapping("/{rentId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDetailsDto addCommentToRent(
            @PathVariable Long rentId,
            @RequestBody @Valid CommentCreateDto createDto
    ) {
        return rentService.addCommentToRent(rentId, createDto);
    }

    @PostMapping("/{rentId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public RentItemDetailsDto addRentItemToRent(
            @PathVariable Long rentId,
            @RequestBody @Valid RentItemCreateDto createDto
    ) {
        return rentService.addRentItemToRent(rentId, createDto);
    }

    @PutMapping("/{rentId}/items/{rentItemId}")
    public RentItemDetailsDto updateRentItem(
            @PathVariable Long rentId,
            @PathVariable Long rentItemId,
            @RequestBody @Valid RentItemUpdateDto updateDto
    ) {
        return rentService.updateRentItem(rentId, rentItemId, updateDto);
    }

    @DeleteMapping("/{rentId}/items/{rentItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRentItem(
            @PathVariable Long rentId,
            @PathVariable Long rentItemId
    ) {
        rentService.deleteRentItem(rentId, rentItemId);
    }

    @GetMapping("/{rentId}/validate")
    public List<RentValidationIssueDto> validateRent(@PathVariable Long rentId) {
        return rentService.validateRent(rentId);
    }

    @PostMapping("/{rentId}/pdf")
    @Secured(RoleConstants.MEMBER_CANDIDATE)
    public ResponseEntity<byte[]> getRentPdf(
            @PathVariable final Long rentId,
            @RequestBody @Valid RentPdfCreateDto createDto
    ) {
        var pdfBytes = rentService.getRentPdf(rentId, createDto);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rent-" + rentId + ".pdf\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(pdfBytes);
    }

}
