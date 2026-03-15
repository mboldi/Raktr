package hu.bsstudio.raktr.dal.entity;

import hu.bsstudio.raktr.dal.value.ConfigDataType;
import hu.bsstudio.raktr.dal.value.ConfigKey;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "configs")
public class Config {

    @Id
    @Enumerated(EnumType.STRING)
    private ConfigKey key;

    private String value;

    @Enumerated(EnumType.STRING)
    private ConfigDataType dataType;

}
