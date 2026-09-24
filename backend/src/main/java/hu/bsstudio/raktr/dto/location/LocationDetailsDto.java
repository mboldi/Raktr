package hu.bsstudio.raktr.dto.location;

import hu.bsstudio.raktr.dto.user.UserSummaryDto;
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

    private UserSummaryDto createdBy;

    private OffsetDateTime updatedAt;

    private UserSummaryDto updatedBy;

}
