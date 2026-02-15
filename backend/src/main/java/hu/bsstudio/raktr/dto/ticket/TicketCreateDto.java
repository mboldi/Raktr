package hu.bsstudio.raktr.dto.ticket;

import hu.bsstudio.raktr.dal.value.ProblemSeverity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateDto {

    @NotBlank
    private String description;

    @NotNull
    private ProblemSeverity severity;

    @Min(1)
    @NotNull
    private Long scannableId;

}
