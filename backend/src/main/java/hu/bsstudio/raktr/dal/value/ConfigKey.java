package hu.bsstudio.raktr.dal.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfigKey {

    RENT_TEAM_NAME(ConfigDataType.STRING),
    RENT_TEAM_LEADER_NAME(ConfigDataType.STRING),
    RENT_FIRST_SIGNER_NAME(ConfigDataType.STRING),
    RENT_FIRST_SIGNER_TITLE(ConfigDataType.STRING),
    RENT_SECOND_SIGNER_NAME(ConfigDataType.STRING),
    RENT_SECOND_SIGNER_TITLE(ConfigDataType.STRING),
    FORCE_EAN8(ConfigDataType.BOOLEAN);

    private final ConfigDataType dataType;

}
