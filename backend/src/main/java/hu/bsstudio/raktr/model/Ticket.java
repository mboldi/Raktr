package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import hu.bsstudio.raktr.dal.value.ProblemSeverity;
import hu.bsstudio.raktr.dal.value.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Builder
    public Ticket(Long id, @NotBlank String body, @NotNull Date dateOfWriting, @NotNull User writer,
                  TicketStatus status, Scannable scannableOfProblem, ProblemSeverity severity, List<Comment> comments) {
        super(id, body, dateOfWriting, writer);
        this.status = status;
        this.scannableOfProblem = scannableOfProblem;
        this.severity = severity;
        this.comments = comments;
    }

    public void addComment(final Comment comment) {
        comments.add(comment);
    }

    public void removeComment(final Comment comment) {
        comments.remove(comment);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class TicketBuilder {
    }
}
