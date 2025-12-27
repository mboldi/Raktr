package hu.bsstudio.raktr.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rent")
public class RentProperties {

    private String groupName;

    private String groupLeader;

    private String firstSignerName;

    private String firstSignerTitle;

    private String secondSignerName;

    private String secondSignerTitle;

}
