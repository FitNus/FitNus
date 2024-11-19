package com.sparta.service.testdata.schedule;

import com.sparta.common.config.JwtUtil;
import com.sparta.common.config.RedisUserService;
import com.sparta.common.enums.UserRole;
import com.sparta.service.center.entity.Center;
import com.sparta.service.center.repository.CenterBulkRepository;
import com.sparta.service.fitness.entity.Fitness;
import com.sparta.service.fitness.repository.FitnessBulkRepository;
import com.sparta.service.schedule.entity.Schedule;
import com.sparta.service.schedule.repository.ScheduleBulkRepository;
import com.sparta.service.timeslot.entity.Timeslot;
import com.sparta.service.timeslot.repository.TimeslotBulkRepository;
import com.sparta.user.user.entity.User;
import com.sparta.user.user.repository.UserBulkRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class ScheduleDummyDataTest {

    @Autowired
    private CenterBulkRepository centerBulkRepository;

    @Autowired
    private FitnessBulkRepository fitnessBulkRepository;

    @Autowired
    private TimeslotBulkRepository timeslotBulkRepository;

    @Autowired
    private ScheduleBulkRepository scheduleBulkRepository;

    @Autowired
    private UserBulkRepository userBulkRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUserService redisUserService;

    //    @Test
//    public void createTokenDummyTest() {
//        try {
//            FileWriter writer = new FileWriter("token_data.txt");
//
//            for (int i = 1; i <= 1000; i++) {
//                Long userId = (long) i;  // 사용자 id
//                String role = UserRole.USER.name();  // 역할
//                String nickname = "헬창" + i;
//                String email = "test" + "i" + "@test.com";
//                String accessToken = jwtUtil.createAccessToken(userId, email, role, nickname);
//                String refreshToken = jwtUtil.createRefreshToken(userId);
//                redisUserService.saveTokens(String.valueOf(userId), accessToken, refreshToken);
//                String aToken = jwtUtil.substringToken(accessToken);
//                String rToken = jwtUtil.substringToken(refreshToken);
//                String data = String.format("%s,%s\n", "Bearer%20" + aToken, "Bearer%20" + rToken);
//
//                writer.write(data);
//            }
//
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    @Test
    public void createCenterData() {
        Random random = new Random();
        double minLatitude = 33.11;
        double maxLatitude = 38.61;
        double minLongitude = 124.39;
        double maxLongitude = 131.52;
        List<Center> centerList = new ArrayList<>();

        int id = 1;

        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 5000; j++, id++) {
                Center center = new Center();
                double randomLatitude =
                        minLatitude + random.nextDouble() * (maxLatitude - minLatitude);
                double randomLongitude =
                        minLongitude + random.nextDouble() * (maxLongitude - minLongitude);
                ReflectionTestUtils.setField(center, "centerName", "헬스장" + (i + 1));
                ReflectionTestUtils.setField(center, "ownerId", (long) id + 1);
                ReflectionTestUtils.setField(center, "latitude", randomLatitude);
                ReflectionTestUtils.setField(center, "longitude", randomLongitude);

                centerList.add(center);
            }

            centerBulkRepository.saveAll(centerList);
        }
    }

    @Test
    public void createAllData() {
        List<Center> centerList = new ArrayList<>();

        for (int i = 0; i < 5000; i++) {
            Center center = new Center();
            ReflectionTestUtils.setField(center, "centerName", "헬스장" + (i + 1));
            ReflectionTestUtils.setField(center, "ownerId", (long) i + 1);

            centerList.add(center);
        }

        centerBulkRepository.saveAll(centerList);

        List<Fitness> fitnessList = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < 5000; i++) {
            Center center = new Center();
            ReflectionTestUtils.setField(center, "id", (long) i + 1);
            Fitness fitness = new Fitness();
            ReflectionTestUtils.setField(fitness, "fitnessName", "헬스");
            ReflectionTestUtils.setField(fitness, "id", (long) i + 1);
            ReflectionTestUtils.setField(fitness, "requiredCoupon", random.nextInt(10) + 1);
            ReflectionTestUtils.setField(fitness, "center", center);

            fitnessList.add(fitness);
        }

        fitnessBulkRepository.saveAll(fitnessList);

        LocalDateTime now = LocalDateTime.now().minusDays(7);
        List<Timeslot> timeslots = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            List<Timeslot> timeslotList = new ArrayList<>();
            LocalDateTime startTime = now.plusDays(i - 1);

            for (int j = 0; j < 5000; j++) {
                Fitness fitness = fitnessList.get(j);
                ReflectionTestUtils.setField(fitness, "id", fitness.getId());
                Timeslot timeslot = new Timeslot();
                ReflectionTestUtils.setField(timeslot, "fitness", fitness);
                ReflectionTestUtils.setField(timeslot, "startTime", startTime);
                ReflectionTestUtils.setField(timeslot, "maxPeople", 50);

                timeslotList.add(timeslot);
                timeslots.add(timeslot);
            }

            timeslotBulkRepository.saveAll(timeslotList);
        }

        long id = 1;

        for (int i = 1; i <= 6; i++) {
            List<User> userList = new ArrayList<>();

            for (int j = 1; j <= 5000; j++, id++) {
                User user = new User();
                ReflectionTestUtils.setField(user, "nickname", "헬창" + id);
                ReflectionTestUtils.setField(user, "email", "test" + id + "@test.com");
                ReflectionTestUtils.setField(user, "password", "test" + id);
                ReflectionTestUtils.setField(user, "userRole", UserRole.USER);
                userList.add(user);
            }

            userBulkRepository.saveAll(userList);
        }

        for (int i = 1; i <= 30000; i++) {
            List<Schedule> scheduleList = new ArrayList<>();

            for (int k = 1; k <= 60; k++) {
                long timeslotId = random.nextInt(150000);
                Timeslot timeslot = timeslots.get((int) timeslotId);
                Schedule schedule = new Schedule();
                ReflectionTestUtils.setField(schedule, "userId", (long) i);
                ReflectionTestUtils.setField(schedule, "scheduleName",
                        timeslot.getFitness().getFitnessName());
                ReflectionTestUtils.setField(schedule, "startTime", timeslot.getStartTime());
                ReflectionTestUtils.setField(schedule, "requiredCoupon",
                        timeslot.getFitness().getRequiredCoupon());
                ReflectionTestUtils.setField(schedule, "timeslotId", timeslotId);

                scheduleList.add(schedule);
            }

            scheduleBulkRepository.saveAll(scheduleList);
        }
    }
}
