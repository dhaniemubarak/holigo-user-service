package id.holigo.services.common.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeviceDto {
    private Long id;

    private Long version;

    private DeviceTypeEnum deviceType;

    private String deviceId;

    private Double latitude;

    private Double longitude;

    private String manufacturer;

    private String model;

    private String osVersion;
}
