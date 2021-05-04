package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "comment")
@JsonDeserialize(builder = Comment.CommentBuilder.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Comment {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String body;

    @NotNull
    private Date dateOfWriting;

    @ManyToOne
    @NotNull
    private User writer;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CommentBuilder {
    }
}
