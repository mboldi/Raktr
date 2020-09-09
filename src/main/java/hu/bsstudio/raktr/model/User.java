package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@JsonSerialize
@JsonDeserialize(builder = User.Builder.class)
@NoArgsConstructor
@Data
public class User {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String ldapUid;

    @NotBlank
    private String nickName;

    @NotNull
    private String familyName;

    @NotNull
    private String givenName;

    @NotNull
    private String personalId;

    @NotNull
    private StudioRank rank;

    public User(final Builder builder) {
        this.id = builder.id;
        this.ldapUid = builder.ldapUid;
        this.nickName = builder.nickName;
        this.familyName = builder.familyName;
        this.givenName = builder.givenName;
        this.personalId = builder.personalId;
        this.rank = builder.rank;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private String ldapUid;
        private String nickName;
        private String familyName;
        private String givenName;
        private String personalId;
        private StudioRank rank;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withLdapUid(final String ldapUid) {
            this.ldapUid = ldapUid;
            return this;
        }

        public Builder withNickName(final String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder withFamilyName(final String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Builder withGivenName(final String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Builder withPersonalId(final String personalId) {
            this.personalId = personalId;
            return this;
        }

        public Builder withRank(final StudioRank rank) {
            this.rank = rank;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
