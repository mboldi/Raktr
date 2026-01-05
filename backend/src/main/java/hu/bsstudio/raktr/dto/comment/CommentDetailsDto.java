package hu.bsstudio.raktr.dto.comment;

import hu.bsstudio.raktr.dto.user.UserAuditDetailsDto;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CommentDetailsDto {

    private Long id;

    private String body;

    private UserAuditDetailsDto createdBy;

    private OffsetDateTime createdAt;

    private UserAuditDetailsDto updatedBy;

    private OffsetDateTime updatedAt;

}
