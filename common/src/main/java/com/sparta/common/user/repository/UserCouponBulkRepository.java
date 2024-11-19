package com.sparta.common.user.repository;

import com.sparta.common.user.entity.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCouponBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<UserCoupon> userCouponList) {
        String sql = "INSERT INTO user_coupon (user_id, quantity, used_quantity, expiration_date, purchase_date) VALUES (?, ?, ?,?,?)";

        jdbcTemplate.batchUpdate(sql,
                userCouponList,
                userCouponList.size(),
                (PreparedStatement ps, UserCoupon userCoupon) -> {
                    ps.setLong(1, userCoupon.getUser().getId());
                    ps.setInt(2, userCoupon.getQuantity());
                    ps.setInt(3, userCoupon.getUsedQuantity());
                    ps.setObject(4, userCoupon.getExpirationDate());
                    ps.setObject(5, userCoupon.getPurchaseDate());
                });
    }
}
