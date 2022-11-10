package id.holigo.services.holigouserservice;

import id.holigo.services.holigouserservice.config.ImageKitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import id.holigo.services.holigouserservice.config.FileStorageProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class, ImageKitProperties.class})
public class HoligoUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoligoUserServiceApplication.class, args);
	}

}
