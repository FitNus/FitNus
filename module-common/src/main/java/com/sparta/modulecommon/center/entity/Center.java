package com.sparta.modulecommon.center.entity;

import com.sparta.modulecommon.center.dto.request.CenterSaveRequest;
import com.sparta.modulecommon.center.dto.request.CenterUpdateRequest;
import com.sparta.modulecommon.common.Timestamped;
import com.sparta.modulecommon.fitness.entity.Fitness;
import com.sparta.modulecommon.user.entity.AuthUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "schedule")
public class Center extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //센터장 id
    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "center_name")
    private String centerName;

    @Column(name = "open_time")
    private Integer openTime;

    @Column(name = "close_time")
    private Integer closeTime;

    @OneToMany(mappedBy = "center", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fitness> fitnesses = new ArrayList<>();

    public Center(CenterSaveRequest request, AuthUser authUser) {
        this.centerName = request.getCenterName();
        this.openTime = request.getOpenTime();
        this.closeTime = request.getCloseTime();
        this.ownerId = authUser.getId();
    }

    // 정팩메
    public static Center of(CenterSaveRequest request, AuthUser authUser) {
        return new Center(request, authUser);
    }

    // 메소드
    public void update(CenterUpdateRequest request) {
        this.centerName = request.getCenterName();
        this.openTime = request.getOpenTime();
        this.closeTime = request.getCloseTime();
    }
}
