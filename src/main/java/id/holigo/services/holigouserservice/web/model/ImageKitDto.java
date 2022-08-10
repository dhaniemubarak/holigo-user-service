package id.holigo.services.holigouserservice.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageKitDto {

    private String fileName;

    private String fileId;

    private String fileType;

    private String url;

}
