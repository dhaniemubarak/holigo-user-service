package id.holigo.services.holigouserservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "imagekit")
@Getter
@Setter
public class ImageKitProperties {
    
    private String urlEndPoint;

    private String privateKey;

    private String publicKey;

}
