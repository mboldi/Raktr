package hu.bsstudio.raktr.dto.rentitem;

import hu.bsstudio.raktr.dal.value.BackStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentItemUpdateDto {

    @NotNull
    private BackStatus status;

    @Min(1)
    @NotNull
    private Integer quantity;

}
