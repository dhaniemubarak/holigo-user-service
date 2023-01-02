package id.holigo.services.holigouserservice.web.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfluencerFollowerDto implements Serializable {

    private Integer holigoFollower;
}
