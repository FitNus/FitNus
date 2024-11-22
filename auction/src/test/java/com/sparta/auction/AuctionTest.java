//package com.sparta.auction;
//
//import com.sparta.common.config.JwtUtil;
//import com.sparta.common.config.RedisUserService;
//import com.sparta.common.enums.UserRole;
//import com.sparta.common.user.entity.User;
//import com.sparta.common.user.entity.UserCoupon;
//import com.sparta.common.user.repository.UserBulkRepository;
//import com.sparta.common.user.repository.UserCouponBulkRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//public class AuctionTest {
//
//    @Autowired
//    private RedisUserService redisUserService;
//    @Autowired
//    private JwtUtil jwtUtil;
//    @Autowired
//    private UserBulkRepository userBulkRepository;
//    @Autowired
//    private UserCouponBulkRepository userCouponBulkRepository;
//
//
//    @Test
//    public void placeBidDummyTest() {
//        try {
//            FileWriter writer = new FileWriter("bid_data.txt");
//            long bid = 1;
//
//            for (int j = 1; j <= 1000; j++, bid++) {
//                Long userId = (long) j;  // 사용자 id
//                String role = UserRole.USER.name();  // 역할
//                String nickname = "헬창" + j;
//                String email = "test" + "i" + "@test.com";
//                String accessToken = jwtUtil.createAccessToken(userId, email, role, nickname);
//                String refreshToken = jwtUtil.createRefreshToken(userId);
//                redisUserService.saveTokens(String.valueOf(userId), accessToken, refreshToken);
//                String aToken = jwtUtil.substringToken(accessToken);
//                String rToken = jwtUtil.substringToken(refreshToken);
//                String data = String.format("%s,%s,%s,%s\n", 1, bid, "Bearer%20" + aToken, "Bearer%20" + rToken);
//
//                writer.write(data);
//            }
//
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void initiateUserCoupon() {
//        long id = 1;
//
//        for (int i = 1; i <= 2; i++) {
//            List<User> userList = new ArrayList<>();
//
//            for (int j = 1; j <= 1000; j++, id++) {
//                User user = new User();
//                ReflectionTestUtils.setField(user, "nickname", "헬창" + id);
//                ReflectionTestUtils.setField(user, "email", "test" + id + "@test.com");
//                ReflectionTestUtils.setField(user, "password", "test" + id);
//                ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
//                userList.add(user);
//            }
//            userBulkRepository.saveAll(userList);
//
//            List<UserCoupon> userCouponList = new ArrayList<>();
//            long userId = 1;
//            for (int j = 1; j <= 1000; j++, userId++) {
//                User user = new User();
//                ReflectionTestUtils.setField(user, "id", (Long) userId);
//                UserCoupon userCoupon = new UserCoupon();
//                ReflectionTestUtils.setField(userCoupon, "user", user);
//                ReflectionTestUtils.setField(userCoupon, "quantity", 10000);
//                ReflectionTestUtils.setField(userCoupon, "usedQuantity", 0);
//                ReflectionTestUtils.setField(userCoupon, "purchaseDate", LocalDateTime.now());
//                ReflectionTestUtils.setField(userCoupon, "expirationDate", LocalDateTime.now().plusMonths(1));
//                userCouponList.add(userCoupon);
//            }
//
//            userCouponBulkRepository.saveAll(userCouponList);
//        }
//    }
//}