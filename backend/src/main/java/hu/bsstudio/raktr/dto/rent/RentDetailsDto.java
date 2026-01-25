package hu.bsstudio.raktr.dto.rent;

import hu.bsstudio.raktr.dal.value.RentType;
import hu.bsstudio.raktr.dto.comment.CommentDetailsDto;
import hu.bsstudio.raktr.dto.rentitem.RentItemDetailsDto;
import hu.bsstudio.raktr.dto.user.UserAuditDetailsDto;
import hu.bsstudio.raktr.dto.user.UserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentDetailsDto {

    private Long id;

    private RentType type;

    private String destination;

    private UserDetailsDto issuer;

    private String renterName;

    private OffsetDateTime outDate;

    private OffsetDateTime expectedReturnDate;

    private OffsetDateTime actualReturnDate;

    private Boolean closed;

    private Boolean deleted;

    private List<RentItemDetailsDto> rentItems;

    private List<CommentDetailsDto> comments;

    private OffsetDateTime createdAt;

    private UserAuditDetailsDto createdBy;

    private OffsetDateTime updatedAt;

    private UserAuditDetailsDto updatedBy;

}
