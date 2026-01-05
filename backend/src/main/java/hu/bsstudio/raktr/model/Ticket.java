package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import hu.bsstudio.raktr.dal.entity.Comment;
import hu.bsstudio.raktr.dal.entity.User;
import hu.bsstudio.raktr.dal.value.ProblemSeverity;
import hu.bsstudio.raktr.dal.value.TicketStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode()
@Entity
@Table(name = "ticket")
@JsonDeserialize(builder = Ticket.TicketBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class Ticket {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String body;

    @NotNull
    private Date dateOfWriting;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private User writer;

    @NotNull
    private TicketStatus status;

    @ManyToOne
    private Scannable scannableOfProblem;

    @NotNull
    private ProblemSeverity severity;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> comments;

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
