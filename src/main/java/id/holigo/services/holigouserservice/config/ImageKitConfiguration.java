package id.holigo.services.holigouserservice.config;

import io.imagekit.sdk.ImageKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageKitConfiguration {
    
    @Autowired
    void setupImageKit(ImageKitProperties imageKitProperties){

		ImageKit imageKit=ImageKit.getInstance();
        io.imagekit.sdk.config.Configuration config = new io.imagekit.sdk.config.Configuration();
        config.setUrlEndpoint(imageKitProperties.getUrlEndPoint());
        config.setPrivateKey(imageKitProperties.getPrivateKey());
        config.setPublicKey(imageKitProperties.getPublicKey());
    
        imageKit.setConfig(config);

    }

}
