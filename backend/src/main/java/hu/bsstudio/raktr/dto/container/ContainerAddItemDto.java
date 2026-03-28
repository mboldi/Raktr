package hu.bsstudio.raktr.dto.container;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerAddItemDto {

    @NotNull
    @Min(1)
    private Long deviceId;

    @NotNull
    @Min(1)
    private Integer quantity;

}
