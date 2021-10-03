package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comment")
@JsonDeserialize(builder = Comment.CommentBuilder.class)
@AllArgsConstructor
@Builder
@Data
public class Comment extends Commentable {

    @JsonPOJOBuilder(withPrefix = "")
    public static class CommentBuilder {
    }
}
