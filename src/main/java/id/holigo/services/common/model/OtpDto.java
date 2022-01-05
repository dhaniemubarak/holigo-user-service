package id.holigo.services.common.model;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpDto {
    private Long id;

    private String encryptKey;

    private Long userId;

    private String phoneNumber;

    private OtpTypeEnum type;

    private Timestamp expiredAt;

    private byte attemptGranted;

    private String oneTimePassword;

    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp createdAt;

    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp updatedAt;

    private OtpStatusEnum status;
}
