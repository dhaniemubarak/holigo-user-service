package id.holigo.services.holigouserservice.web.model;

import java.io.Serializable;

import id.holigo.services.holigouserservice.domain.ReferralStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReferralDto implements Serializable {

    private Long id;

    private String referral;

    private String name;

    private String photoProfileUrl;

    private Boolean isOfficialAccount;

    private String note;

    private ReferralStatusEnum status;
}
