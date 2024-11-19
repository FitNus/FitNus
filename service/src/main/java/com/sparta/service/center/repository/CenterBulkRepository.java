package com.sparta.service.center.repository;

import com.sparta.service.center.entity.Center;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CenterBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Center> centerList) {
        String sql = "INSERT INTO center (center_name, owner_id, latitude, longitude, location) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql,
                centerList,
                centerList.size(),
                (PreparedStatement ps, Center center) -> {
                    ps.setString(1, center.getCenterName());
                    ps.setLong(2, center.getOwnerId());
                    ps.setDouble(3, center.getLatitude());
                    ps.setDouble(4, center.getLongitude());
                    ps.setObject(5, center.getLocation());
                });
    }
}
