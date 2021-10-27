package id.holigo.services.holigouserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import id.holigo.services.holigouserservice.domain.UserPersonal;

public interface UserPersonalRepository extends JpaRepository<UserPersonal, Long> {

}
