package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "GeneralData")
@JsonDeserialize(builder = GeneralData.GeneralDataBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GeneralData extends DomainAuditModel {
    @Id
    @NotBlank
    @Column(unique = true)
    private String key;

    private String data;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GeneralDataBuilder {}
}
