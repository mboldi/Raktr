package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ticket")
@JsonDeserialize(builder = Ticket.TicketBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class Ticket extends Commentable {

    @NotNull
    private TicketStatus status;

    @ManyToOne
    private Scannable scannableOfProblem;

    @NotNull
    private ProblemSeverity severity;

    @Builder
    public Ticket(Long id, @NotBlank String body, @NotNull Date dateOfWriting, @NotNull User writer,
                  TicketStatus status, Scannable scannableOfProblem, ProblemSeverity severity) {
        super(id, body, dateOfWriting, writer);
        this.status = status;
        this.scannableOfProblem = scannableOfProblem;
        this.severity = severity;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class TicketBuilder {
    }
}
