package id.holigo.services.holigouserservice.services;

import org.springframework.data.domain.PageRequest;

import id.holigo.services.holigouserservice.web.model.UserDevicePaginate;

public interface UserDeviceService {
    UserDevicePaginate listDevice(Long userId, PageRequest pageRequest);
}
