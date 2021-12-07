package id.holigo.services.holigouserservice.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import id.holigo.services.common.model.AccountStatusEnum;
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

    @Column(length = 100, columnDefinition = "varchar(100)")
    private String name;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String phoneNumber;

    @Nullable
    private String email;

    private EmailStatusEnum emailStatus;

    private AccountStatusEnum accountStatus;

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

    @Nullable
    private String mobileToken;

    @Singular
    @ManyToMany(fetch = EAGER)
    @JoinTable(name = "user_authority", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
                    @JoinColumn(name = "authority_id", referencedColumnName = "id") })
    private Set<Authority> authorities;

    @Builder.Default
    @OneToMany(mappedBy = "user")
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

    @Builder.Default
    private Boolean enabled = true;

    public void setPin(String value) {
        this.pin = new BCryptPasswordEncoder().encode(value);
    }

}
