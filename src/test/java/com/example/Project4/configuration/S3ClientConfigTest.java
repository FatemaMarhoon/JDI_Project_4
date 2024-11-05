package com.example.Project4.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class S3ClientConfigTest {

    @InjectMocks
    private S3ClientConfig s3ClientConfig;

    @BeforeEach
    void setUp() {
        // Set the awsRegion field in the S3ClientConfig instance
        ReflectionTestUtils.setField(s3ClientConfig, "awsRegion", "us-east-1");
    }

    @Test
    void testS3ClientCreation() {
        S3AsyncClient s3AsyncClient = s3ClientConfig.s3Client();
        assertNotNull(s3AsyncClient, "S3AsyncClient should not be null");
    }

    @Test
    void testS3PresignerCreation() {
        S3Presigner s3Presigner = s3ClientConfig.s3Presigner();
        assertNotNull(s3Presigner, "S3Presigner should not be null");
    }
}
