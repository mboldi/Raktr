package hu.bsstudio.raktr.dto.comment;

import hu.bsstudio.raktr.dto.user.UserSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDetailsDto {

    private Long id;

    private String body;

    private OffsetDateTime createdAt;

    private UserSummaryDto createdBy;

    private OffsetDateTime updatedAt;

    private UserSummaryDto updatedBy;

}
