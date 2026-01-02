package hu.bsstudio.raktr.dto.owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDetailsDto {

    private Integer id;

    private String name;

    private Boolean inSchInventory;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

}
