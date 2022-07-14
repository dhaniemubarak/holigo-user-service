package id.holigo.services.holigouserservice.domain;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.*;

import id.holigo.services.common.model.EmailStatusEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private GenderEnum gender;

    private Date birthDate;

    private String city;

    @OneToOne(mappedBy = "userPersonal", fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToOne
    private UserPersonalPhotoProfile photoProfile;

    public String getName() {
        return user.getName();
    }

    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public EmailStatusEnum getEmailStatus() {
        return user.getEmailStatus();
    }
}
