package id.holigo.services.holigouserservice.web.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import id.holigo.services.common.model.UserDeviceDto;

public class UserDevicePaginate extends PageImpl<UserDeviceDto> {

    @JsonCreator(mode = Mode.PROPERTIES)
    public UserDevicePaginate(@JsonProperty("content") List<UserDeviceDto> content, @JsonProperty("number") int number,
            @JsonProperty("size") int size, @JsonProperty("totalElement") int totalElements,
            @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
            @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort,
            @JsonProperty("first") boolean first, @JsonProperty("numberOfElement") int numberOfElement

    ) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    public UserDevicePaginate(List<UserDeviceDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public UserDevicePaginate(List<UserDeviceDto> content) {
        super(content);
    }

    
}
