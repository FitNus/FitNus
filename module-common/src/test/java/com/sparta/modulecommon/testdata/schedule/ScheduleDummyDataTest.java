//package com.sparta.modulecommon.testdata.schedule;
//
//import com.sparta.modulecommon.center.entity.Center;
//import com.sparta.modulecommon.center.repository.CenterBulkRepository;
//import com.sparta.modulecommon.fitness.entity.Fitness;
//import com.sparta.modulecommon.fitness.repository.FitnessBulkRepository;
//import com.sparta.modulecommon.schedule.repository.ScheduleBulkRepository;
//import com.sparta.modulecommon.timeslot.repository.TimeslotBulkRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//public class ScheduleDummyDataTest {
//
//    @Autowired
//    private CenterBulkRepository centerBulkRepository;
//
//    @Autowired
//    private FitnessBulkRepository fitnessBulkRepository;
//
//    @Autowired
//    private TimeslotBulkRepository timeslotBulkRepository;
//
//    @Autowired
//    private ScheduleBulkRepository scheduleBulkRepository;
//
//    @Test
//    public void createCenterData() {
//        List<Center> centerList = new ArrayList<>();
//
//        for (int i = 0; i < 500; i++) {
//            Center center = new Center();
//            ReflectionTestUtils.setField(center, "centerName", "헬스장" + (i + 1));
//            ReflectionTestUtils.setField(center, "ownerId", (long) i + 1);
//
//            centerList.add(center);
//        }
//
//        centerBulkRepository.saveAll(centerList);
//
//        List<Fitness> fitnessList = new ArrayList<>();
//
//        for (int i = 0; i < 500; i++) {
//            Center center = new Center();
//            ReflectionTestUtils.setField(center, "id", (long) i + 1);
//            Fitness fitness = new Fitness();
//            ReflectionTestUtils.setField(fitness, "fitnessName", "헬스");
//            ReflectionTestUtils.setField(fitness, "requiredCoupon", 3);
//            ReflectionTestUtils.setField(fitness, "center", center);
//
//            fitnessList.add(fitness);
//        }
//
//        fitnessBulkRepository.saveAll(fitnessList);
//
////        for (int i = 1; i <= 30; i++) {
////            List<Timeslot> timeslotList = new ArrayList<>();
////            LocalDateTime startTime = LocalDateTime.now();
////
////            for (int j = 0; j < 500; j++) {
////                Fitness fitness = new Fitness();
////                ReflectionTestUtils.setField(fitness, "id", (long) j + 1);
////                Timeslot timeslot = new Timeslot();
////                ReflectionTestUtils.setField(timeslot, "fitness", fitness);
////                ReflectionTestUtils.setField(timeslot, "startTime", startTime);
////
////                timeslotList.add(timeslot);
////            }
////
////            timeslotBulkRepository.saveAll(timeslotList);
////        }
////
////        for (int i = 1; i <= 30; i++) {
////            for (int j = 1; j <= 500; j++) {
////                Timeslot timeslot = new Timeslot();
////                ReflectionTestUtils.setField(timeslot, "id", (long) j);
////                List<Schedule> scheduleList = new ArrayList<>();
////
////                for (int k = 1; k <= 500; k++) {
////                    Schedule schedule = new Schedule();
////                    ReflectionTestUtils.setField(schedule, "userId", (long) k);
////                    ReflectionTestUtils.setField(schedule, "startTime", LocalDateTime.now().plusDays(i - 1));
////                    ReflectionTestUtils.setField(schedule, "requiredCoupon", 3);
////                    ReflectionTestUtils.setField(schedule, "timeslotId", timeslot.getId());
////
////                    scheduleList.add(schedule);
////                }
////
////                scheduleBulkRepository.saveAll(scheduleList);
////            }
////        }
//    }
//
////    @Test
////    public void createDummyUser() {
////        User user = new User();
////        ReflectionTestUtils.setField(user, "nickname", "헬창");
////
////        List<Timeslot> timeslotList = timeslotRepository.findAllByC
////    }
//}
