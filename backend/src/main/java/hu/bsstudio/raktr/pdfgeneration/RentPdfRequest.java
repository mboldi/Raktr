package hu.bsstudio.raktr.pdfgeneration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonSerialize
@JsonDeserialize(builder = RentPdfRequest.Builder.class)
@NoArgsConstructor
@Data
public class RentPdfRequest {

    private String teamName;
    private String teamLeaderName;

    private String renterFullName;
    private String renterId;

    public RentPdfRequest(final Builder builder) {
        this.teamName = builder.teamName;
        this.teamLeaderName = builder.teamLeaderName;
        this.renterFullName = builder.renterFullName;
        this.renterId = builder.renterId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private String teamName;
        private String teamLeaderName;
        private String renterFullName;
        private String renterId;

        public Builder withTeamName(final String teamName) {
            this.teamName = teamName;
            return this;
        }

        public Builder withTeamLeaderName(final String teamLeaderName) {
            this.teamLeaderName = teamLeaderName;
            return this;
        }

        public Builder withRenterFullName(final String renterFullName) {
            this.renterFullName = renterFullName;
            return this;
        }

        public Builder withRenterId(final String renterId) {
            this.renterId = renterId;
            return this;
        }

        public RentPdfRequest build() {
            return new RentPdfRequest(this);
        }
    }
}
