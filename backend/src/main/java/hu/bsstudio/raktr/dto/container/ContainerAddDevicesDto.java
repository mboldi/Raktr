package hu.bsstudio.raktr.dto.container;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerAddDevicesDto {

    @NotNull
    @Size(min = 1)
    private List<Long> deviceIds;

}
