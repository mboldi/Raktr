package hu.bsstudio.raktr.dto.ticket;

import hu.bsstudio.raktr.dal.value.ProblemSeverity;
import hu.bsstudio.raktr.dal.value.TicketStatus;
import hu.bsstudio.raktr.dto.comment.CommentDetailsDto;
import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import hu.bsstudio.raktr.dto.user.UserAuditDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailsDto {

    private Long id;

    private String description;

    private TicketStatus status;

    private ProblemSeverity severity;

    private Scannable scannable;

    private List<CommentDetailsDto> comments;

    private OffsetDateTime createdAt;

    private UserAuditDetailsDto createdBy;

    private OffsetDateTime updatedAt;

    private UserAuditDetailsDto updatedBy;

    @Data
    public static class Scannable {

        private Long id;

        private String assetTag;

        private String name;

        private ScannableDetailsDto.Owner owner;

        private String manufacturer;

        private String acquisitionSource;

        private LocalDate acquisitionDate;

        private LocalDate warrantyEndDate;

    }

}
