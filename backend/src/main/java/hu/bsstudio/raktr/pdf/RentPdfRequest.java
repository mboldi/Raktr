package hu.bsstudio.raktr.pdf;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentPdfRequest {

    @NotBlank
    private String teamName;

    @NotBlank
    private String teamLeaderName;

    @NotBlank
    private String renterName;

    @NotBlank
    private String renterId;

    @NotBlank
    private String firstSignerName;

    @NotBlank
    private String firstSignerTitle;

    @NotBlank
    private String secondSignerName;

    @NotBlank
    private String secondSignerTitle;

    private LocalDate deliveryDate;

    private LocalDate returnDate;

    private Map<String, Integer> items;

}
