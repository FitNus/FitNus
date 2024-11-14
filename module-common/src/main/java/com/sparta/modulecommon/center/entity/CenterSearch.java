package com.sparta.modulecommon.center.entity;

import com.sparta.modulecommon.fitness.entity.Fitness;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter
@NoArgsConstructor
@Document(indexName = "center")
@Setting(settingPath = "elasticsearch/elasticsearch-settings.json")
public class CenterSearch {

    @Id
    private Long id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String centerName;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private List<String> fitnessName;

    @Field(type = FieldType.Text)
    private String address;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Double)
    private Double latitude;

    @Field(type = FieldType.Double)
    private Double longitude;

    public CenterSearch(Center center) {
        this.id = center.getId();
        this.centerName = center.getCenterName();
        this.address = center.getAddress();
        this.latitude = center.getLatitude();
        this.longitude = center.getLongitude();
        this.location = new GeoPoint(center.getLatitude(), center.getLongitude());
        this.fitnessName = center.getFitnesses().stream()
                .map(Fitness::getFitnessName)
                .collect(Collectors.toList());
    }
}
