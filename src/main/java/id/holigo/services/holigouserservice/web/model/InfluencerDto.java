package id.holigo.services.holigouserservice.web.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfluencerDto implements Serializable {

    private Integer holigoFollower;

    private String referralCode;
}
