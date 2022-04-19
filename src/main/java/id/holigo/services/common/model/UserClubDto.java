package id.holigo.services.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserClubDto implements Serializable {

    private Long userId;

    private UserGroupEnum userGroup;

    private String name;

    private Integer exp;
}
