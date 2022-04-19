package id.holigo.services.holigouserservice.web.mappers;

import org.mapstruct.Mapper;

import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.web.model.UserPersonalDto;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserPersonalMapper {
    UserPersonalDto userPersonalToUserPersonalDto(UserPersonal userPersonal);

    UserPersonal userPersonalDtoToUserPersonal(UserPersonalDto userPersonalDto);
}
