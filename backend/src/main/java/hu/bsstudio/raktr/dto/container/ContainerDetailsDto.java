package hu.bsstudio.raktr.dto.container;

import hu.bsstudio.raktr.dto.scannable.ScannableDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContainerDetailsDto extends ScannableDetailsDto {

    private Integer totalWeight;

    private List<ContainerItemDetailsDto> items;

}
