package com.sparta.modulecommon.testdata.schedule;

import com.sparta.modulecommon.center.entity.Center;
import com.sparta.modulecommon.center.repository.CenterBulkRepository;
import com.sparta.modulecommon.fitness.entity.Fitness;
import com.sparta.modulecommon.fitness.repository.FitnessBulkRepository;
import com.sparta.modulecommon.schedule.entity.Schedule;
import com.sparta.modulecommon.schedule.repository.ScheduleBulkRepository;
import com.sparta.modulecommon.timeslot.entity.Timeslot;
import com.sparta.modulecommon.timeslot.repository.TimeslotBulkRepository;
import com.sparta.modulecommon.user.entity.User;
import com.sparta.modulecommon.user.repository.UserBulkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Test
    public void createCenterData() {
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
                ReflectionTestUtils.setField(schedule, "scheduleName", timeslot.getFitness().getFitnessName());
                ReflectionTestUtils.setField(schedule, "startTime", timeslot.getStartTime());
                ReflectionTestUtils.setField(schedule, "requiredCoupon", timeslot.getFitness().getRequiredCoupon());
                ReflectionTestUtils.setField(schedule, "timeslotId", timeslotId);

                scheduleList.add(schedule);
            }

            scheduleBulkRepository.saveAll(scheduleList);
        }
    }
}
