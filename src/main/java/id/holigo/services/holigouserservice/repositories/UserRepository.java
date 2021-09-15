package id.holigo.services.holigouserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import id.holigo.services.holigouserservice.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
