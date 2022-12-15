package id.holigo.services.common.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeviceDto {

    private Long id;

    private String mobileToken;

    private Long userId;

    private DeviceTypeEnum deviceType;

    private String deviceId;

    private Double latitude;

    private Double longitude;

    private String manufacturer;

    private String model;

    private String osVersion;
}
