package id.holigo.services.holigouserservice.repositories;

import id.holigo.services.holigouserservice.domain.DeletedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedUserRepository extends JpaRepository<DeletedUser, Long> {
}
