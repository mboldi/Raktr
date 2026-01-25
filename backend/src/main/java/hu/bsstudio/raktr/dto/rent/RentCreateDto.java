package hu.bsstudio.raktr.dto.rent;

import hu.bsstudio.raktr.dal.value.RentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentCreateDto {

    @NotNull
    private RentType type;

    @NotBlank
    private String destination;

    @NotNull
    private UUID issuerId;

    @NotBlank
    private String renterName;

    @NotNull
    private LocalDate outDate;

    @NotNull
    private LocalDate expectedReturnDate;

}
