package id.holigo.services.holigouserservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import id.holigo.services.holigouserservice.domain.UserDevice;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Page<UserDevice> findAllByUserId(Long userId, Pageable pageable);
}
