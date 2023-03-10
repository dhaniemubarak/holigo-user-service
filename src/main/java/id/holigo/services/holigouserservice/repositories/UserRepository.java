package id.holigo.services.holigouserservice.repositories;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import id.holigo.services.holigouserservice.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByEmail(String email);

    List<User> findAllByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    Optional<MobileToken> findMobileTokenById(Long userId);

    @Query("select COUNT(id) from User u where u.parent.id =:parentId GROUP BY u.parent.id")
    Integer getFollower(@Param("parentId") Long parentId);

}
