package hu.bsstudio.raktr.rent.service;

import hu.bsstudio.raktr.comment.mapper.CommentMapper;
import hu.bsstudio.raktr.config.service.ConfigService;
import hu.bsstudio.raktr.dal.entity.Rent;
import hu.bsstudio.raktr.dal.entity.RentItem;
import hu.bsstudio.raktr.dal.entity.Scannable;
import hu.bsstudio.raktr.dal.repository.CommentRepository;
import hu.bsstudio.raktr.dal.repository.RentItemRepository;
import hu.bsstudio.raktr.dal.repository.RentRepository;
import hu.bsstudio.raktr.dal.repository.UserRepository;
import hu.bsstudio.raktr.dal.value.BackStatus;
import hu.bsstudio.raktr.pdf.RentPdfService;
import hu.bsstudio.raktr.rent.mapper.RentItemMapper;
import hu.bsstudio.raktr.rent.mapper.RentMapper;
import hu.bsstudio.raktr.scannable.service.ScannableLookupService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RentServiceTest {

    private final RentRepository rentRepository = mock(RentRepository.class);

    private final RentItemRepository rentItemRepository = mock(RentItemRepository.class);

    private final RentService rentService = new RentService(
            rentRepository,
            rentItemRepository,
            mock(CommentRepository.class),
            mock(UserRepository.class),
            mock(ConfigService.class),
            mock(ScannableLookupService.class),
            mock(RentPdfService.class),
            mock(RentMapper.class),
            mock(RentItemMapper.class),
            mock(CommentMapper.class)
    );

    @Test
    void validateRentIgnoresScannablesThatAreNeitherDeviceNorContainer() {
        var rentItem = new RentItem();
        rentItem.setScannable(new Scannable() {
        });
        rentItem.setStatus(BackStatus.OUT);
        rentItem.setQuantity(1);
        var rent = new Rent();
        rent.setId(1L);
        rent.setRentItems(List.of(rentItem));
        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));

        assertThat(rentService.validateRent(1L)).isEmpty();
        verifyNoInteractions(rentItemRepository);
    }

}
