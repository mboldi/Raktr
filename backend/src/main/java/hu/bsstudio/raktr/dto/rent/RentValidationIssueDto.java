package hu.bsstudio.raktr.dto.rent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RentValidationIssueDto {

    private Long deviceId;

    private String deviceName;

    private int requestedQuantity;

    private int availableQuantity;

    private List<RentValidationSourceDto> sources;

}
