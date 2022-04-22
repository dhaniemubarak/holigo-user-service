package id.holigo.services.holigouserservice.domain;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.*;

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

    @Column(length = 100, columnDefinition = "varchar(100)")
    private String name;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String phoneNumber;

    private String email;

    private EmailStatusEnum emailStatus;

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
}
