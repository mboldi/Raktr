package hu.bsstudio.raktr.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.bsstudio.raktr.model.RentItem;
import java.util.Date;
import lombok.AllArgsConstructor;

@JsonSerialize
@JsonDeserialize
@AllArgsConstructor
public class RentItemWithOutDateAndRentId {
    public RentItem rentItem;

    public Date outDate;

    public Long rentId;
}
