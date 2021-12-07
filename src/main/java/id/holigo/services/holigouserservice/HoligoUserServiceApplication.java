package id.holigo.services.holigouserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import id.holigo.services.holigouserservice.config.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
public class HoligoUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoligoUserServiceApplication.class, args);
	}

}
