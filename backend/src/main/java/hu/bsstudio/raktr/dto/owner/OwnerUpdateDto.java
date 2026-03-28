package hu.bsstudio.raktr.dto.owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerUpdateDto {

    @NotBlank
    private String name;

    @NotNull
    private boolean inSchInventory;

}
