package hu.bsstudio.raktr.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDto {

    @NotBlank
    @Pattern(regexp = "^[a-z0-9_-]+$")
    private String name;

}
