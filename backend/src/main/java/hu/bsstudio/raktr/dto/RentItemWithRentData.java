package hu.bsstudio.raktr.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.model.RentItem;
import lombok.AllArgsConstructor;

@JsonSerialize
@JsonDeserialize
@AllArgsConstructor
public final class RentItemWithRentData {
    public RentItem rentItem;

    public Rent rent;
}
