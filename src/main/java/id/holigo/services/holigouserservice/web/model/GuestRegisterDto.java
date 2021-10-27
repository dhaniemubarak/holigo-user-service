package id.holigo.services.holigouserservice.web.model;

import java.util.List;

import id.holigo.services.common.model.UserDeviceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestRegisterDto {

    private String mobileToken;

    List<UserDeviceDto> userDevices;
}
