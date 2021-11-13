package id.holigo.services.holigouserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import id.holigo.services.holigouserservice.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Authority findByRole(String role);
}
