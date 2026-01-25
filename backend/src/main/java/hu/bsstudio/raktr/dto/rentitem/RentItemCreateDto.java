package hu.bsstudio.raktr.dto.rentitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentItemCreateDto {

    @Min(1)
    @NotNull
    private Long scannableId;

    @Min(1)
    @NotNull
    private Integer quantity;

}
