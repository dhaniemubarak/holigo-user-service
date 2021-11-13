package id.holigo.services.holigouserservice;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import id.holigo.services.holigouserservice.config.FileStorageProperties;
import id.holigo.services.holigouserservice.domain.Authority;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.repositories.AuthorityRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.services.UserService;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
public class HoligoUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoligoUserServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService, UserRepository userRepository,
			AuthorityRepository authorityRepository) {
		return args -> {
			Authority auth = authorityRepository.save(Authority.builder().role("ROLE_GUEST").build());
			authorityRepository.save(Authority.builder().role("ROLE_USER").build());

			Collection<Authority> roles = new ArrayList<>();
			roles.add(auth);
			userRepository.save(User.builder().name("Mochamad Ramdhanie").phoneNumber("6285718187373")
					.oneTimePassword(new BCryptPasswordEncoder().encode("1234")).type("GUEST").authorities(roles)
					.build());
		};
	}

}
