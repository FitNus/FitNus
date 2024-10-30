package com.sparta.fitnus.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String AWS_ACCESS_KEY;
    @Value("${cloud.aws.credentials.secret-key}")
    private String AWS_SECRET_KEY;
    @Value("${cloud.aws.region.static}")
    private String AWS_REGION;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY,
                AWS_SECRET_KEY);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(AWS_REGION)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
