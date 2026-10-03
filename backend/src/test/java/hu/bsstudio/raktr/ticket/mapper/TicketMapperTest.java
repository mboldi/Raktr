package hu.bsstudio.raktr.ticket.mapper;

import hu.bsstudio.raktr.dal.entity.Container;
import hu.bsstudio.raktr.dal.entity.Device;
import hu.bsstudio.raktr.dal.entity.Owner;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TicketMapperTest {

    private final TicketMapper mapper = Mappers.getMapper(TicketMapper.class);

    @Test
    void mapsNullScannableToNull() {
        assertThat(mapper.scannableToDto(null)).isNull();
    }

    @Test
    void mapsContainerWithoutOwnerOrDeviceFields() {
        var container = new Container();
        container.setId(1L);
        container.setAssetTag("CONT-1");
        container.setName("Case");

        var dto = mapper.scannableToDto(container);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getAssetTag()).isEqualTo("CONT-1");
        assertThat(dto.getName()).isEqualTo("Case");
        assertThat(dto.getOwner()).isNull();
        assertThat(dto.getManufacturer()).isNull();
        assertThat(dto.getAcquisitionDate()).isNull();
    }

    @Test
    void mapsDeviceWithOwnerAndDeviceFields() {
        var owner = new Owner();
        owner.setId(5L);
        owner.setName("BSS");
        var device = new Device();
        device.setId(2L);
        device.setName("Camera");
        device.setOwner(owner);
        device.setManufacturer("Sony");
        device.setAcquisitionSource("Grant");
        device.setAcquisitionDate(LocalDate.of(2024, 1, 2));
        device.setWarrantyEndDate(LocalDate.of(2026, 1, 2));

        var dto = mapper.scannableToDto(device);

        assertThat(dto.getOwner().getId()).isEqualTo(5L);
        assertThat(dto.getOwner().getName()).isEqualTo("BSS");
        assertThat(dto.getManufacturer()).isEqualTo("Sony");
        assertThat(dto.getAcquisitionSource()).isEqualTo("Grant");
        assertThat(dto.getAcquisitionDate()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(dto.getWarrantyEndDate()).isEqualTo(LocalDate.of(2026, 1, 2));
    }

}
