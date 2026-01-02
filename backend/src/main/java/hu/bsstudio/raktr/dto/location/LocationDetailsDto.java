package hu.bsstudio.raktr.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDetailsDto {

    private String name;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

}
