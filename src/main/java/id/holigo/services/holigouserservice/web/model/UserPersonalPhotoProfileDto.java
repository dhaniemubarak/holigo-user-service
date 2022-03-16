package id.holigo.services.holigouserservice.web.model;

import javax.validation.constraints.Null;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPersonalPhotoProfileDto {

    @Null
    private Long id;

    private String fileName;

    private String fileDownloadUri;

    private String fileType;

    private Long size;

}
