package hu.bsstudio.raktr.dto.rentitem;

import hu.bsstudio.raktr.dal.value.BackStatus;
import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import hu.bsstudio.raktr.dto.user.UserAuditDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentItemDetailsDto {

    private Long id;

    private ScannableDetailsDto scannable;

    private BackStatus status;

    private Integer quantity;

    private OffsetDateTime createdAt;

    private UserAuditDetailsDto createdBy;

    private OffsetDateTime updatedAt;

    private UserAuditDetailsDto updatedBy;

}
