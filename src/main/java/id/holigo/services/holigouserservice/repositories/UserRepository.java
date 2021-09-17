package id.holigo.services.holigouserservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import id.holigo.services.holigouserservice.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByEmail(String email);

    List<User> findAllByPhoneNumber(String phoneNumber);

}
