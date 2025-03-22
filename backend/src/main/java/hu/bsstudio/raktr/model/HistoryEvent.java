package hu.bsstudio.raktr.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "historyEvents")
@JsonSerialize()
@JsonDeserialize()
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HistoryEvent implements Serializable {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timeOfEvent", nullable = false, updatable = false)
    @CreatedDate
    private Date timeOfEvent;

    @ManyToOne(fetch = LAZY)
    private User user;

    @NotNull
    private EntityOperationType operationType;

    private EntityType entityType;

    private Long entityId;

    @NotNull
    private boolean allowedOperation;

    private String description;
}
