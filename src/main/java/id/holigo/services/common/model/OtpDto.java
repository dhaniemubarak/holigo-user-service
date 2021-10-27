package id.holigo.services.common.model;

import java.security.Timestamp;

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

    private Long version;

    private String key;

    private String encryptKey;

    private Long userId;

    private String phoneNumber;

    private String type;

    private Timestamp expiredAt;

    private byte attemptGranted;

    private Timestamp createdAt;
    
    private Timestamp updatedAt;

    private String status;
}
