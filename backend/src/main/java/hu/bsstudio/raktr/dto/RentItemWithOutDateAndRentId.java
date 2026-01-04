package hu.bsstudio.raktr.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.bsstudio.raktr.model.RentItem;
import lombok.AllArgsConstructor;

import java.util.Date;

@JsonSerialize
@JsonDeserialize
@AllArgsConstructor
public class RentItemWithOutDateAndRentId {
    public RentItem rentItem;

    public Date outDate;

    public Long rentId;
}
