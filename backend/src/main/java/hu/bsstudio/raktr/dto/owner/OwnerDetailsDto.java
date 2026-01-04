package hu.bsstudio.raktr.dto.owner;

import hu.bsstudio.raktr.dto.user.UserAuditDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDetailsDto {

    private Integer id;

    private String name;

    private Boolean inSchInventory;

    private UserAuditDetailsDto createdBy;

    private OffsetDateTime createdAt;

    private UserAuditDetailsDto updatedBy;

    private OffsetDateTime updatedAt;

}
