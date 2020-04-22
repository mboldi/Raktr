package hu.bsstudio.raktr.pdfgeneration;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RentPdfData {

    private String teamName;
    private String teamLeaderName;

    private String renterName;
    private String renterId;

    private String outDate;
    private String backDate;

    private String fileName;

    private Map<String, Integer> items;

    public RentPdfData(final Builder builder) {
        this.teamName = builder.teamName;
        this.teamLeaderName = builder.teamLeaderName;
        this.renterName = builder.renterName;
        this.renterId = builder.renterId;
        this.outDate = builder.outDate;
        this.backDate = builder.backDate;
        this.fileName = builder.fileName;
        this.items = builder.items;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private String teamName;
        private String teamLeaderName;
        private String renterName;
        private String renterId;
        private String outDate;
        private String backDate;
        private String fileName;
        private Map<String, Integer> items;

        public Builder withTeamName(final String teamName) {
            this.teamName = teamName;
            return this;
        }

        public Builder withTeamLeaderName(final String teamLeaderName) {
            this.teamLeaderName = teamLeaderName;
            return this;
        }

        public Builder withRenterName(final String renterName) {
            this.renterName = renterName;
            return this;
        }

        public Builder withRenterId(final String renterId) {
            this.renterId = renterId;
            return this;
        }

        public Builder withOutDate(final String outDate) {
            this.outDate = outDate;
            return this;
        }

        public Builder withBackDate(final String backDate) {
            this.backDate = backDate;
            return this;
        }

        public Builder withFileName(final String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withItems(final Map<String, Integer> items) {
            this.items = items;
            return this;
        }

        public RentPdfData build() {
            return new RentPdfData(this);
        }
    }
}
