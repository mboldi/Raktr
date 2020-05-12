package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("checkstyle:FinalClass")
@Entity
@Data
@JsonSerialize
@JsonDeserialize(builder = RentItem.Builder.class)
@NoArgsConstructor
public class RentItem extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JoinColumn
    @ManyToOne
    private Scannable scannable;

    @NotNull
    private BackStatus backStatus;

    @NotNull
    private Integer outQuantity;

    private RentItem(final Builder builder) {
        this.id = builder.id;
        this.backStatus = builder.backStatus;
        this.scannable = builder.scannable;
        this.outQuantity = builder.outQuantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private Scannable scannable;
        private BackStatus backStatus;
        private Integer outQuantity;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withScannable(final Scannable scannable) {
            this.scannable = scannable;
            return this;
        }

        public Builder withBackStatus(final BackStatus backStatus) {
            this.backStatus = backStatus;
            return this;
        }

        public Builder withOutQuantity(final Integer outQuantity) {
            this.outQuantity = outQuantity;
            return this;
        }

        public RentItem build() {
            return new RentItem(this);
        }
    }
}
