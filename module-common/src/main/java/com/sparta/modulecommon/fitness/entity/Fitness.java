package com.sparta.modulecommon.fitness.entity;

import com.sparta.modulecommon.center.entity.Center;
import com.sparta.modulecommon.fitness.dto.request.FitnessRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "fitness")
public class Fitness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @Column(name = "fitness_name")
    private String fitnessName;

    @Column(name = "required_coupon")
    private Integer requiredCoupon;

    public Fitness(FitnessRequest request, Center center) {
        this.center = center;
        this.fitnessName = request.getFitnessName();
        this.requiredCoupon = request.getRequiredCoupon();
    }

    public static Fitness of(FitnessRequest request, Center center) {
        return new Fitness(request, center);
    }

    public void update(FitnessRequest request) {
        this.fitnessName = request.getFitnessName();
        this.requiredCoupon = request.getRequiredCoupon();
    }
}
