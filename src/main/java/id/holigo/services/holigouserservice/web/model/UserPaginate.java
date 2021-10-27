package id.holigo.services.holigouserservice.web.model;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import id.holigo.services.common.model.UserDto;

public class UserPaginate extends PageImpl<UserDto> {

    public UserPaginate(List<UserDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public UserPaginate(List<UserDto> content) {
        super(content);
    }

}
