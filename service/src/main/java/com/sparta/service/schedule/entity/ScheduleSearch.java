package com.sparta.service.schedule.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@NoArgsConstructor
@Document(indexName = "schedule")
@Setting(settingPath = "elasticsearch/schedule-settings.json")
@Mapping(mappingPath = "elasticsearch/schedule-mapping.json")
public class ScheduleSearch {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Long)
    private Long timeslotId;

    @Field(type = FieldType.Long)
    private Long clubId;

    @Field(type = FieldType.Text)
    private String scheduleName;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private LocalDateTime startTime;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private LocalDateTime endTime;

    @Field(type = FieldType.Integer)
    private Integer requiredCoupon;

    // 검색 최적화를 위한 추가 필드
    @Field(type = FieldType.Integer)
    private Integer year;

    @Field(type = FieldType.Integer)
    private Integer month;

    @Field(type = FieldType.Integer)
    private Integer day;

    public ScheduleSearch(Schedule schedule) {
        this.id = schedule.getId();
        this.userId = schedule.getUserId();
        this.timeslotId = schedule.getTimeslotId();
        this.clubId = schedule.getClubId();
        this.scheduleName = schedule.getScheduleName();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.requiredCoupon = schedule.getRequiredCoupon();

        // 검색을 위한 날짜 정보 설정
        if (schedule.getStartTime() != null) {
            this.year = schedule.getStartTime().getYear();
            this.month = schedule.getStartTime().getMonthValue();
            this.day = schedule.getStartTime().getDayOfMonth();
        }
    }
}
