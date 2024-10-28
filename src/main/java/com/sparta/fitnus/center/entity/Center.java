package com.sparta.fitnus.center.entity;

import com.sparta.fitnus.center.dto.request.CenterSaveRequest;
import com.sparta.fitnus.center.dto.request.CenterUpdateRequest;
import com.sparta.fitnus.user.entity.AuthUser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //센터장 id
    private Long ownerId;

    private String centerName;

    private Integer openTime;

    private Integer closeTime;


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
