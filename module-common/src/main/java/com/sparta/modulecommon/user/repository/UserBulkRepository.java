package com.sparta.modulecommon.user.repository;

import com.sparta.modulecommon.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<User> userList) {
        String sql = "INSERT INTO user (nickname, email, password, user_role) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql,
                userList,
                userList.size(),
                (PreparedStatement ps, User user) -> {
                    ps.setString(1, user.getNickname());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPassword());
                    ps.setString(4, user.getUserRole().getUserRole());
                });
    }
}
