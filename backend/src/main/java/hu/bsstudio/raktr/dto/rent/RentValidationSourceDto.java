package hu.bsstudio.raktr.dto.rent;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentValidationSourceDto {

    private Long containerId;

    private int quantity;

}
