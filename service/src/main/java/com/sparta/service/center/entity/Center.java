package com.sparta.service.center.entity;

import com.sparta.common.Timestamped;
import com.sparta.common.user.dto.AuthUser;
import com.sparta.service.center.dto.request.CenterSaveRequest;
import com.sparta.service.center.dto.request.CenterUpdateRequest;
import com.sparta.service.fitness.entity.Fitness;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.List;

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
