package hu.bsstudio.raktr.dto.scannable;

import hu.bsstudio.raktr.dto.user.UserAuditDetailsDto;
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

    private Boolean publicRentable;

    private Boolean deleted;

    private String category;

    private String location;

    private Owner owner;

    private OffsetDateTime createdAt;

    private UserAuditDetailsDto createdBy;

    private OffsetDateTime updatedAt;

    private UserAuditDetailsDto updatedBy;

    @Data
    public static class Owner {

        private Integer id;

        private String name;

    }

}
