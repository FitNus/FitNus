package com.sparta.modulecommon.center.entity;

import com.sparta.modulecommon.center.dto.request.CenterSaveRequest;
import com.sparta.modulecommon.center.dto.request.CenterUpdateRequest;
import com.sparta.modulecommon.common.Timestamped;
import com.sparta.modulecommon.fitness.entity.Fitness;
import com.sparta.modulecommon.user.entity.AuthUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "center")
public class Center extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //센터장 id
    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "center_name")
    private String centerName;

    @Column(name = "address")
    private String address;

    @Column(name = "open_time")
    private Integer openTime;

    @Column(name = "close_time")
    private Integer closeTime;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location")
    private Point location;

    @OneToMany(mappedBy = "center", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fitness> fitnesses = new ArrayList<>();

    public Center(CenterSaveRequest request, AuthUser authUser) {
        this.centerName = request.getCenterName();
        this.address = request.getAddress();
        this.openTime = request.getOpenTime();
        this.closeTime = request.getCloseTime();
        this.ownerId = authUser.getId();
    }

    public static Center of(CenterSaveRequest request, AuthUser authUser, double latitude,
            double longitude) {
        Center center = new Center(request, authUser);
        center.latitude = latitude;
        center.longitude = longitude;
        center.location = new Point(latitude, longitude);
        return center;
    }

    public void update(CenterUpdateRequest request) {
        this.centerName = request.getCenterName();
        this.openTime = request.getOpenTime();
        this.closeTime = request.getCloseTime();
    }
}