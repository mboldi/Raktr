package hu.bsstudio.raktr.dto.category;

import hu.bsstudio.raktr.dto.user.UserAuditDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailsDto {

    private String name;

    private UserAuditDetailsDto createdBy;

    private OffsetDateTime createdAt;

    private UserAuditDetailsDto updatedBy;

    private OffsetDateTime updatedAt;

}
