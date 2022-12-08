package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
