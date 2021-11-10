package id.holigo.services.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthenticationDto {

    private Long id;

    private String phoneNumber;

    private AccountStatusEnum accountStatus;

    private String type;

    private String oneTimePassword;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private Boolean credentialsNonExpired;

    private Boolean enabled;
}
