package hu.bsstudio.raktr.dto.location;

import hu.bsstudio.raktr.dto.user.UserAuditDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDetailsDto {

    private String name;

    private UserAuditDetailsDto createdBy;

    private OffsetDateTime createdAt;

    private UserAuditDetailsDto updatedBy;

    private OffsetDateTime updatedAt;

}
