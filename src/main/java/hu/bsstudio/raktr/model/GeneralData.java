package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "GeneralData")
@JsonDeserialize(builder = GeneralData.Builder.class)
@NoArgsConstructor
@Data
public class GeneralData extends DomainAuditModel {
    @Id
    @NotBlank
    @Column(unique = true)
    private String key;

    private String data;

    GeneralData(final Builder builder) {
        this.key = builder.key;
        this.data = builder.data;
    }

    final Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private String key;
        private String data;

        private Builder() {
        }

        public Builder withKey(final String key) {
            this.key = key;
            return this;
        }

        public Builder withData(final String data) {
            this.data = data;
            return this;
        }

        public GeneralData build() {
            return new GeneralData(this);
        }
    }
}
