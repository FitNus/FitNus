package com.sparta.common.user.repository;

import com.sparta.common.user.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
//    @Query("SELECT u FROM UserCoupon u WHERE u.expirationDate < :currentDate AND (u.quantity - u.usedQuantity) > :remainingQuantity")
//    List<UserCoupon> findExpiredCouponsWithRemainingQuantity(
//            @Param("currentDate") LocalDate currentDate,
//            @Param("remainingQuantity") int remainingQuantity);

    @Query("SELECT u FROM UserCoupon u WHERE u.expirationDate < :currentDateTime AND (u.quantity - u.usedQuantity) > 0")
    List<UserCoupon> findExpiredCouponsWithRemainingQuantity(
            @Param("currentDateTime") LocalDateTime currentDateTime);
}
