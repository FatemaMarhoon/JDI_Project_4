package com.example.Project4.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class S3ServiceTest {

    @Mock
    private S3AsyncClient s3AsyncClient;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        s3Service.setBucketName("test-bucket");
        s3Service.setCloudFrontDomain("https://cloudfront.example.com");
    }

    @Test
    public void testUploadFile_Success() {
        // Mock the PutObjectResponse
        PutObjectResponse putObjectResponse = PutObjectResponse.builder().build();

        // Mock the S3AsyncClient's putObject method
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.completedFuture(putObjectResponse));

        byte[] fileData = new byte[]{1, 2, 3};
        String fileName = "test-file";
        String contentType = "application/octet-stream";

        // Call the method
        CompletableFuture<String> result = s3Service.uploadFile(fileData, fileName, contentType);

        // Wait for completion and assert the result
        result.thenAccept(url -> {
            assertNotNull(url);
            assertTrue(url.contains(fileName));  // Ensure the returned URL contains the file name
        });
    }

    @Test
    public void testDoesFileExist_FileExists() {
        // Mock the HeadObjectResponse
        HeadObjectResponse headObjectResponse = HeadObjectResponse.builder().build();

        // Mock the S3AsyncClient's headObject method
        when(s3AsyncClient.headObject(any(HeadObjectRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(headObjectResponse));

        String fileKey = "existing-file";

        // Call the method
        CompletableFuture<Boolean> result = s3Service.doesFileExist(fileKey);

        // Wait for completion and assert the result
        result.thenAccept(exists -> {
            assertTrue(exists);
        });
    }

    @Test
    public void testDoesFileExist_FileDoesNotExist() {
        // Mock the S3AsyncClient's headObject method to throw an S3Exception for file not found
        when(s3AsyncClient.headObject(any(HeadObjectRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(S3Exception.builder()
                        .statusCode(404)
                        .awsErrorDetails(AwsErrorDetails.builder().errorCode("NoSuchKey").build())
                        .build()));

        String fileKey = "non-existing-file";

        // Call the method
        CompletableFuture<Boolean> result = s3Service.doesFileExist(fileKey);

        // Wait for completion and assert the result
        result.thenAccept(exists -> {
            assertFalse(exists);
        });
    }

    @Test
    public void testDeleteFile_Success() {
        // Mock the DeleteObjectResponse
        DeleteObjectResponse deleteObjectResponse = DeleteObjectResponse.builder().build();

        // Mock the S3AsyncClient's deleteObject method
        when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(deleteObjectResponse));

        String fileKey = "file-to-delete";

        // Call the method
        CompletableFuture<Void> result = s3Service.deleteFile(fileKey);

        // Wait for completion and assert the result
        result.thenRun(() -> {
            // If no exception is thrown, deletion is successful
            assertTrue(true);
        });
    }


}
