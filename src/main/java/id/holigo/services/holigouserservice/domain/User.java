package id.holigo.services.holigouserservice.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import id.holigo.services.common.model.EmailStatusEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import id.holigo.services.common.model.AccountStatusEnum;
import id.holigo.services.common.model.UserGroupEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long officialId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    @JsonBackReference
    private User parent;

    @Column(length = 100, columnDefinition = "varchar(100)")
    private String name;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String phoneNumber;

    private String email;

    @Enumerated(EnumType.STRING)
    private EmailStatusEnum emailStatus;

    private AccountStatusEnum accountStatus;

    @Convert(converter = UserGroupEnumConverter.class)
    private UserGroupEnum userGroup;

    @Column(length = 64)
    private String verificationCode;

    @Nullable
    private Timestamp emailVerifiedAt;

    @Nullable
    private String pin;

    @Nullable
    private String oneTimePassword;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String type;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Timestamp deletedAt;

    @Column(nullable = true)
    private String mobileToken;

    @Singular
    @ManyToMany(fetch = EAGER)
    @JoinTable(name = "user_authority", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "authority_id", referencedColumnName = "id")})
    @JsonBackReference
    private Set<Authority> authorities;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<UserDevice> userDevices = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    private UserPersonal userPersonal;

    public void addUserDevice(UserDevice userDevice) {
        userDevices.add(userDevice);
    }

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    private Boolean enabled;

    public void setPin(String value) {
        if (value != null) {
            this.pin = new BCryptPasswordEncoder().encode(value);
        } else {
            this.pin = null;
        }
    }

    @Transient
    private String referral;

    @Column(columnDefinition = "bit(1) default 0")
    private Boolean isOfficialAccount;

    @OneToOne(mappedBy = "user")
    private UserReferral userReferral;

    public Boolean isEnabled() {
        return this.getEnabled();
    }

}
