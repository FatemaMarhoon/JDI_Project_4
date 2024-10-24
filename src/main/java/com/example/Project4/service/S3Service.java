package com.example.Project4.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

@Service
public class S3Service {

    private final S3AsyncClient s3AsyncClient;

    @Setter
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Setter
    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Autowired
    public S3Service(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    /**
     * Retrieves the CDN (CloudFront) URL for a file stored in S3.
     *
     * @param fileKey The key (name) of the file in S3.
     * @return A URL to retrieve the file via CloudFront.
     */
    public String getCDNFileUrl(String fileKey) {
        return String.format("%s/%s", cloudFrontDomain, fileKey);
    }

    /**
     * Uploads a file to S3 asynchronously, appending a unique ID to the file name to avoid overriding.
     *
     * @param fileData The byte array of the file to be uploaded.
     * @param fileName The name of the file to be stored in the S3 bucket.
     * @param contentType The MIME type of the file.
     * @return A CompletableFuture containing the URL of the uploaded file.
     */
    public CompletableFuture<String> uploadFile(byte[] fileData, String fileName, String contentType) {
        // Generate a unique ID to append to the file name
        String uniqueId = UUID.randomUUID().toString();
        String uniqueFileName = fileName + "-" + uniqueId;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(contentType)
                .build();

        return s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(fileData))
                .exceptionally(e -> {
                    throw new RuntimeException(e.getMessage());
                })
                .thenApply(putObjectResponse -> getCDNFileUrl(uniqueFileName));
    }

    /**
     * Checks if a file exists in the S3 bucket.
     *
     * @param fileKey The key (name) of the file in S3.
     * @return True if the file exists, otherwise false.
     */
    public CompletableFuture<Boolean> doesFileExist(String fileKey) {
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        return s3AsyncClient.headObject(headRequest)
                .thenApply(response -> true) // File exists
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof S3Exception &&
                            ((S3Exception) ex.getCause()).statusCode() == 404 &&
                            "NoSuchKey".equals(((S3Exception) ex.getCause()).awsErrorDetails().errorCode())) {
                        return false; // File does not exist
                    }
                    throw new RuntimeException("Error checking file existence: " + ex.getMessage());
                });
    }


    /**
     * Deletes a file from the S3 bucket.
     *
     * @param fileKey The key (name) of the file to be deleted.
     * @return A CompletableFuture indicating the result of the deletion operation.
     */
    public CompletableFuture<Void> deleteFile(String fileKey) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        return s3AsyncClient.deleteObject(deleteRequest)
                .thenRun(() -> {
                    // Log or handle the successful deletion if needed
                })
                .exceptionally(e -> {
                    throw new RuntimeException("Failed to delete file: " + e.getMessage());
                });
    }

    /**
     * Asynchronously retrieves a paginated list of file URLs stored in the S3 bucket.
     *
     * @param page The page number (starting from 0).
     * @param size The number of files to return per page.
     * @return A CompletableFuture containing a paginated list of file URLs.
     */
    public CompletableFuture<List<String>> getPaginatedFileUrls(int page, int size) {
        ListObjectsV2Request.Builder listRequestBuilder = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .maxKeys(size);  // Limit the number of files per page

        // Handle pagination through continuation tokens
        return getPaginatedFiles(listRequestBuilder, page, size, null)
                .exceptionally(e -> {
                    throw new RuntimeException("Failed to retrieve paginated file list: " + e.getMessage());
                });
    }

    private CompletableFuture<List<String>> getPaginatedFiles(ListObjectsV2Request.Builder listRequestBuilder, int page, int size, String continuationToken) {
        if (continuationToken != null) {
            listRequestBuilder.continuationToken(continuationToken);
        }

        return s3AsyncClient.listObjectsV2(listRequestBuilder.build())
                .thenCompose(response -> {
                    if (page > 0 && response.isTruncated()) {
                        // If the response is truncated, move to the next page recursively until we reach the correct one
                        return getPaginatedFiles(listRequestBuilder, page - 1, size, response.nextContinuationToken());
                    } else {
                        // Extract the file URLs from the current page
                        List<String> fileUrls = new ArrayList<>();
                        response.contents().forEach(summary -> {
                            String fileKey = summary.key();
                            fileUrls.add(getCDNFileUrl(fileKey));
                        });
                        return CompletableFuture.completedFuture(fileUrls);
                    }
                });
    }

}
