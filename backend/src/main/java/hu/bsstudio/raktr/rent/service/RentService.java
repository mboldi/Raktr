package hu.bsstudio.raktr.rent.service;

import hu.bsstudio.raktr.comment.mapper.CommentMapper;
import hu.bsstudio.raktr.config.service.ConfigService;
import hu.bsstudio.raktr.dal.entity.Device;
import hu.bsstudio.raktr.dal.entity.Rent;
import hu.bsstudio.raktr.dal.entity.RentItem;
import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.entity.User;
import hu.bsstudio.raktr.dal.repository.CommentRepository;
import hu.bsstudio.raktr.dal.repository.RentItemRepository;
import hu.bsstudio.raktr.dal.repository.RentRepository;
import hu.bsstudio.raktr.dal.repository.UserRepository;
import hu.bsstudio.raktr.dal.value.BackStatus;
import hu.bsstudio.raktr.dal.value.ConfigKey;
import hu.bsstudio.raktr.dal.value.RentType;
import hu.bsstudio.raktr.dto.comment.CommentCreateDto;
import hu.bsstudio.raktr.dto.comment.CommentDetailsDto;
import hu.bsstudio.raktr.dto.rent.RentCreateDto;
import hu.bsstudio.raktr.dto.rent.RentDetailsDto;
import hu.bsstudio.raktr.dto.rent.RentPdfCreateDto;
import hu.bsstudio.raktr.dto.rent.RentUpdateDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemCreateDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemDetailsDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemUpdateDto;
import hu.bsstudio.raktr.exception.EntityAlreadyExistsException;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import hu.bsstudio.raktr.exception.NotAvailableQuantityException;
import hu.bsstudio.raktr.pdf.RentPdfRequest;
import hu.bsstudio.raktr.pdf.RentPdfService;
import hu.bsstudio.raktr.rent.mapper.RentItemMapper;
import hu.bsstudio.raktr.rent.mapper.RentMapper;
import hu.bsstudio.raktr.scannable.service.ScannableLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;

    private final RentItemRepository rentItemRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final ConfigService configService;

    private final ScannableLookupService lookupService;

    private final RentPdfService rentPdfService;

    private final RentMapper rentMapper;

    private final RentItemMapper rentItemMapper;

    private final CommentMapper commentMapper;

    @Transactional
    public List<RentDetailsDto> listRents(Boolean deleted) {
        var rents = rentRepository.findAllByDeleted(deleted);
        return rents.stream().map(rentMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public List<RentDetailsDto> getRentsByScannableId(Long scannableId) {
        var rents = rentRepository.findAllByRentItemsScannableId(scannableId);
        return rents.stream().map(rentMapper::entityToDetailsDto).toList();
    }

    @Transactional
    public RentDetailsDto createRent(RentCreateDto createDto) {
        var rent = rentMapper.createDtoToEntity(createDto);

        var issuer = getIssuer(createDto.getIssuerId());
        rent.setIssuer(issuer);

        rent = rentRepository.saveAndFlush(rent);

        log.info("Created Rent with ID [{}]", rent.getId());

        return rentMapper.entityToDetailsDto(rent);
    }

    @Transactional
    public RentDetailsDto getRentById(Long rentId) {
        var rent = getRent(rentId);
        return rentMapper.entityToDetailsDto(rent);
    }

    @Transactional
    public RentDetailsDto updateRent(Long rentId, RentUpdateDto updateDto) {
        var rent = getRent(rentId);

        rentMapper.updateDtoToEntity(rent, updateDto);

        var issuer = getIssuer(updateDto.getIssuerId());
        rent.setIssuer(issuer);

        rentRepository.saveAndFlush(rent);

        log.info("Updated Rent with ID [{}]", rentId);

        return rentMapper.entityToDetailsDto(rent);
    }

    @Transactional
    public void deleteRent(Long rentId) {
        var rent = getRent(rentId);
        rent.setDeleted(true);
        rentRepository.saveAndFlush(rent);
        log.info("Deleted Rent with ID [{}]", rentId);
    }

    @Transactional
    public void restoreRent(Long rentId) {
        var rent = getRent(rentId);
        rent.setDeleted(false);
        rentRepository.saveAndFlush(rent);
        log.info("Restored Rent with ID [{}]", rentId);
    }

    @Transactional
    public CommentDetailsDto addCommentToRent(Long rentId, CommentCreateDto createDto) {
        var rent = getRent(rentId);

        var comment = commentMapper.createDtoToEntity(createDto);
        comment = commentRepository.saveAndFlush(comment);

        rent.getComments().add(comment);

        log.info("Added Comment [{}] to Rent [{}]", comment.getId(), rentId);

        return commentMapper.entityToDetailsDto(comment);
    }

    @Transactional
    public RentItemDetailsDto addRentItemToRent(Long rentId, RentItemCreateDto createDto) {
        var rent = getRent(rentId);

        if (rentItemRepository.existsByRentAndScannableId(rent, createDto.getScannableId())) {
            throw new EntityAlreadyExistsException(RentItem.class, createDto.getScannableId());
        }

        var rentItem = rentItemMapper.createDtoToEntity(createDto);

        var scannable = lookupService.getScannable(createDto.getScannableId());
        validateDeviceAvailability(scannable, rent, createDto.getQuantity(), null);

        rentItem.setRent(rent);
        rentItem.setScannable(scannable);
        rentItem.setStatus(rent.getType() == RentType.SIMPLE ? BackStatus.OUT : BackStatus.PENDING);

        rentItem = rentItemRepository.saveAndFlush(rentItem);

        log.info("Added RentItem [{}] to Rent [{}]", rentItem.getId(), rentId);

        return rentItemMapper.entityToDetailsDto(rentItem);
    }

    @Transactional
    public RentItemDetailsDto updateRentItem(Long rentId, Long rentItemId, RentItemUpdateDto updateDto) {
        var rent = getRent(rentId);
        var rentItem = getRentItem(rentItemId, rent);

        rentItemMapper.updateDtoToEntity(rentItem, updateDto);

        validateDeviceAvailability(rentItem.getScannable(), rent, updateDto.getQuantity(), rentItem.getId());

        rentItem = rentItemRepository.saveAndFlush(rentItem);

        log.info("Updated RentItem [{}] to Rent [{}]", rentItem.getId(), rentId);

        closeRentIfAllReturned(rent, null);

        return rentItemMapper.entityToDetailsDto(rentItem);
    }

    @Transactional
    public void deleteRentItem(Long rentId, Long rentItemId) {
        var rent = getRent(rentId);
        var rentItem = getRentItem(rentItemId, rent);

        rentItemRepository.delete(rentItem);

        log.info("Deleted RentItem [{}] from Rent [{}]", rentItemId, rentId);

        closeRentIfAllReturned(rent, rentItemId);
    }

    @Transactional
    public ResponseEntity<byte[]> getRentPdf(Long rentId, RentPdfCreateDto createDto) {
        var rent = getRent(rentId);

        var items = new LinkedHashMap<String, Integer>();
        for (var rentItem : rent.getRentItems()) {
            items.merge(rentItem.getScannable().getName(), rentItem.getQuantity(), Integer::sum);
        }

        var request = RentPdfRequest.builder()
                .teamName(configService.getString(ConfigKey.RENT_TEAM_NAME))
                .teamLeaderName(configService.getString(ConfigKey.RENT_TEAM_LEADER_NAME))
                .renterName(rent.getRenterName())
                .renterId(createDto.getRenterId())
                .firstSignerName(configService.getString(ConfigKey.RENT_FIRST_SIGNER_NAME))
                .firstSignerTitle(configService.getString(ConfigKey.RENT_FIRST_SIGNER_TITLE))
                .secondSignerName(configService.getString(ConfigKey.RENT_SECOND_SIGNER_NAME))
                .secondSignerTitle(configService.getString(ConfigKey.RENT_SECOND_SIGNER_TITLE))
                .deliveryDate(rent.getOutDate())
                .returnDate(rent.getExpectedReturnDate())
                .items(items)
                .build();

        var pdfBytes = rentPdfService.generateRentPdf(request);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rent-" + rentId + ".pdf\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(pdfBytes);
    }

    private Rent getRent(Long rentId) {
        return rentRepository.findById(rentId)
                .orElseThrow(() -> new EntityNotFoundException(Rent.class, rentId));
    }

    private RentItem getRentItem(Long rentItemId, Rent rent) {
        return rentItemRepository.findByIdAndRent(rentItemId, rent)
                .orElseThrow(() -> new EntityNotFoundException(RentItem.class, rentItemId));
    }

    private User getIssuer(UUID issuerId) {
        return userRepository.findById(issuerId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, issuerId));
    }

    private void closeRentIfAllReturned(Rent rent, Long excludeRentItemId) {
        var allReturned = rent.getRentItems().stream()
                .filter(item -> !item.getId().equals(excludeRentItemId))
                .allMatch(item -> item.getStatus() == BackStatus.RETURNED);

        if (allReturned) {
            rent.setClosed(true);
            rentRepository.saveAndFlush(rent);
            log.info("All items returned, closed Rent [{}]", rent.getId());
        }
    }

    private void validateDeviceAvailability(Scannable scannable, Rent rent, int requestedQuantity, Long excludeRentItemId) {
        if (!(scannable instanceof Device device)) {
            return;
        }

        var bookedQuantity = rentItemRepository.sumBookedQuantity(
                device.getId(),
                rent.getOutDate(),
                rent.getExpectedReturnDate(),
                excludeRentItemId
        );

        var availableQuantity = device.getQuantity() - bookedQuantity;

        if (requestedQuantity > availableQuantity) {
            throw new NotAvailableQuantityException(device.getName(), requestedQuantity, availableQuantity);
        }
    }

}
