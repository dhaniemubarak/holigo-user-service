package id.holigo.services.common.model;

import lombok.*;

import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPointDto implements Serializable {

    @Null
    private UUID id;

    private Long userId;

    private Integer point;
}
