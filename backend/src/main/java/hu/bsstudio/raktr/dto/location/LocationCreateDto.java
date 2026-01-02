package hu.bsstudio.raktr.dto.location;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationCreateDto {

    @NotBlank
    private String name;

}
