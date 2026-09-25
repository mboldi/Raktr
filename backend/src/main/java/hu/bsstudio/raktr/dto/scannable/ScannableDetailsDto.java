package hu.bsstudio.raktr.dto.scannable;

import hu.bsstudio.raktr.dto.user.UserSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScannableDetailsDto {

    private Long id;

    private String assetTag;

    private String barcode;

    private String name;

    private Integer weight;

    private boolean publicRentable;

    private boolean deleted;

    private String category;

    private String location;

    private Owner owner;

    private OffsetDateTime createdAt;

    private UserSummaryDto createdBy;

    private OffsetDateTime updatedAt;

    private UserSummaryDto updatedBy;

    @Data
    public static class Owner {

        private Long id;

        private String name;

    }

}
