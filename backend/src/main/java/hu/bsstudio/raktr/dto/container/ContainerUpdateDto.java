package hu.bsstudio.raktr.dto.container;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerUpdateDto {

    @NotBlank
    private String assetTag;

    @NotBlank
    private String barcode;

    @NotBlank
    private String name;

    @Min(1)
    @NotNull
    private Integer weight;

    private Boolean publicRentable;

    @NotBlank
    private String categoryName;

    @NotBlank
    private String locationName;

    @Min(1)
    @NotNull
    private Integer ownerId;

}
