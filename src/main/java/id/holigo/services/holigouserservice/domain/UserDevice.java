package id.holigo.services.holigouserservice.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.holigo.services.common.model.DeviceTypeEnum;
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
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private DeviceTypeEnum deviceType;

    @Column(length = 50, columnDefinition = "varchar(50)")
    private String deviceId;

    private Double latitude;

    private Double longitude;

    @Column(length = 50, columnDefinition = "varchar(50)")
    private String manufacturer;

    @Column(length = 50, columnDefinition = "varchar(50)")
    private String model;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String osVersion;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
