package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ticket")
@JsonDeserialize(builder = Ticket.TicketBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Ticket {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    private Scannable scannableOfProblem;

    @NotBlank
    private String descriptionOfProblem;

    @NotNull
    private TicketStatus status;

    @NotNull
    private ProblemSeverity severity;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TicketBuilder {
    }
}
