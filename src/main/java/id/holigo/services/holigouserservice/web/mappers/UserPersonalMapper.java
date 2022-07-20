package id.holigo.services.holigouserservice.web.mappers;

import org.mapstruct.Mapper;

import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.web.model.UserPersonalDto;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserPersonalMapper {
    UserPersonalDto userPersonalToUserPersonalDto(UserPersonal userPersonal);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserPersonal userPersonalDtoToUserPersonal(UserPersonalDto userPersonalDto);
}
