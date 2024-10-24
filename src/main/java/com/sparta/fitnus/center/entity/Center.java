package com.sparta.fitnus.center.entity;

import com.sparta.fitnus.center.dto.request.CenterSaveRequest;
import com.sparta.fitnus.center.dto.request.CenterUpdateRequest;
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

    private String nickName;

    private String centerName;

    private int price;

    private Integer openTime;

    private Integer closeTime;

    private int maxCapacity;
    private int availableCapacity;

    public Center(CenterSaveRequest request) {
        this.nickName = request.getNickName();
        this.centerName = request.getCenterName();
        this.price = request.getPrice();
        this.openTime = request.getOpenTime();
        this.closeTime = request.getCloseTime();
        this.maxCapacity = request.getMaxCapacity();
        this.availableCapacity = request.getAvailableCapacity();
    }

    // 정팩메
    public static Center of(CenterSaveRequest request) {
        return new Center(request);
    }

    // 메소드
    public void update(CenterUpdateRequest request) {
        this.nickName = request.getNickName();
        this.centerName = request.getCenterName();
        this.price = request.getPrice();
        this.openTime = request.getOpenTime();
        this.closeTime = request.getCloseTime();
        this.maxCapacity = request.getMaxCapacity();
        this.availableCapacity = request.getMaxCapacity(); //처음에는 MaxCapacity()와 인원수 같다.
    }


}
