package com.sparta.fitnus.fitness.entity;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.fitness.dto.request.FitnessRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Fitness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    private String fitnessName;

    private int price;

    private int maxCapacity;

    private int availableCapacity;

    public Fitness(FitnessRequest request, Center center) {
        this.center = center;
        this.fitnessName = request.getFitnessName();
        this.price = center.getPrice();
        this.maxCapacity = center.getMaxCapacity();
        this.availableCapacity = center.getAvailableCapacity();
    }

    public static Fitness of(FitnessRequest request, Center center) {
        return new Fitness(request, center);
    }
}
