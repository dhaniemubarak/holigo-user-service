package id.holigo.services.holigouserservice.services;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import id.holigo.services.holigouserservice.domain.UserDevice;
import id.holigo.services.holigouserservice.repositories.UserDeviceRepository;
import id.holigo.services.holigouserservice.web.mappers.UserDeviceMapper;
import id.holigo.services.holigouserservice.web.model.UserDevicePaginate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;

    private final UserDeviceMapper userDeviceMapper;

    @Override
    public UserDevicePaginate listDevice(Long userId, PageRequest pageRequest) {
        UserDevicePaginate userDevicePaginate;
        Page<UserDevice> userDevicePage;

        userDevicePage = userDeviceRepository.findAllByUserId(userId, pageRequest);

        userDevicePaginate = new UserDevicePaginate(userDevicePage.getContent().stream()
                .map(userDeviceMapper::userDeviceToUserDeviceDto).collect(Collectors.toList()));
        return userDevicePaginate;
    }

}
