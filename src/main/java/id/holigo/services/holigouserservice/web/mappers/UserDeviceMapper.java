package id.holigo.services.holigouserservice.web.mappers;

import org.mapstruct.Mapper;

import id.holigo.services.common.model.UserDeviceDto;
import id.holigo.services.holigouserservice.domain.UserDevice;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserDeviceMapper {

    UserDeviceDto userDeviceToUserDeviceDto(UserDevice userDevice);

    UserDevice userDeviceDtoToUserDevice(UserDeviceDto userDeviceDto);
}
