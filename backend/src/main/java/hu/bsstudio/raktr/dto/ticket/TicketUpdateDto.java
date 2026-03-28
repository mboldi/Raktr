package hu.bsstudio.raktr.dto.ticket;

import hu.bsstudio.raktr.dal.value.ProblemSeverity;
import hu.bsstudio.raktr.dal.value.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateDto {

    @NotBlank
    private String description;

    @NotNull
    private TicketStatus status;

    @NotNull
    private ProblemSeverity severity;

}
