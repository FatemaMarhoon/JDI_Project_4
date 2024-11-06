package com.example.Project4.controller;

import com.example.Project4.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileControllerTest {

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllFiles_Success() {
        // Mock the S3Service's method for paginated file URLs
        List<String> mockFileUrls = List.of("https://cloudfront.example.com/file1", "https://cloudfront.example.com/file2");
        when(s3Service.getPaginatedFileUrls(0, 10)).thenReturn(CompletableFuture.completedFuture(mockFileUrls));

        // Call the controller method
        CompletableFuture<ResponseEntity<List<String>>> responseFuture = fileController.getAllFiles(0, 10);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockFileUrls, response.getBody());
        });
    }

    @Test
    public void testGetAllFiles_Failure() {
        // Mock failure in retrieving file URLs
        when(s3Service.getPaginatedFileUrls(0, 10))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Failed to retrieve file list")));

        // Call the controller method
        CompletableFuture<ResponseEntity<List<String>>> responseFuture = fileController.getAllFiles(0, 10);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.getBody().get(0).contains("Failed to retrieve file list"));
        });
    }

    @Test
    public void testRetrieveFile_FileExists() {
        String fileKey = "file1";
        String mockCdnUrl = "https://cloudfront.example.com/file1";

        // Mock the file existence check and the URL retrieval
        when(s3Service.doesFileExist(fileKey)).thenReturn(CompletableFuture.completedFuture(true));
        when(s3Service.getCDNFileUrl(fileKey)).thenReturn(mockCdnUrl);

        // Call the controller method
        CompletableFuture<ResponseEntity<String>> responseFuture = fileController.retrieveFile(fileKey);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockCdnUrl, response.getBody());
        });
    }

    @Test
    public void testRetrieveFile_FileNotFound() {
        String fileKey = "non-existing-file";

        // Mock the file existence check to return false
        when(s3Service.doesFileExist(fileKey)).thenReturn(CompletableFuture.completedFuture(false));

        // Call the controller method
        CompletableFuture<ResponseEntity<String>> responseFuture = fileController.retrieveFile(fileKey);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("File not found: " + fileKey, response.getBody());
        });
    }

    @Test
    public void testUploadFiles_Success() {
        // Mock the upload behavior of S3Service
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-file");
        when(mockFile.getContentType()).thenReturn("application/octet-stream");

        when(s3Service.uploadFile(any(byte[].class), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture("https://cloudfront.example.com/test-file"));

        MultipartFile[] files = {mockFile};

        // Call the controller method
        CompletableFuture<ResponseEntity<List<String>>> responseFuture = fileController.uploadFiles(files);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("https://cloudfront.example.com/test-file"));
        });
    }

    @Test
    public void testUploadFiles_Failure() {
        // Mock failure in file upload
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-file");
        when(mockFile.getContentType()).thenReturn("application/octet-stream");

        when(s3Service.uploadFile(any(byte[].class), anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Upload failed")));

        MultipartFile[] files = {mockFile};

        // Call the controller method
        CompletableFuture<ResponseEntity<List<String>>> responseFuture = fileController.uploadFiles(files);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.getBody().get(0).contains("Failed to upload one or more files"));
        });
    }

    @Test
    public void testDeleteFile_Success() {
        String fileKey = "file1";

        // Mock file existence check and deletion
        when(s3Service.doesFileExist(fileKey)).thenReturn(CompletableFuture.completedFuture(true));
        when(s3Service.deleteFile(fileKey)).thenReturn(CompletableFuture.completedFuture(null));

        // Call the controller method
        CompletableFuture<ResponseEntity<String>> responseFuture = fileController.deleteFile(fileKey);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("File deleted successfully.", response.getBody());
        });
    }

    @Test
    public void testDeleteFile_FileNotFound() {
        String fileKey = "non-existing-file";

        // Mock file existence check to return false
        when(s3Service.doesFileExist(fileKey)).thenReturn(CompletableFuture.completedFuture(false));

        // Call the controller method
        CompletableFuture<ResponseEntity<String>> responseFuture = fileController.deleteFile(fileKey);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("File not found: " + fileKey, response.getBody());
        });
    }

    @Test
    public void testDeleteFile_Failure() {
        String fileKey = "file1";

        // Mock file existence check and deletion to throw an exception
        when(s3Service.doesFileExist(fileKey)).thenReturn(CompletableFuture.completedFuture(true));
        when(s3Service.deleteFile(fileKey)).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Failed to delete file")));

        // Call the controller method
        CompletableFuture<ResponseEntity<String>> responseFuture = fileController.deleteFile(fileKey);

        // Assert the response
        responseFuture.thenAccept(response -> {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.getBody().contains("Failed to delete file"));
        });
    }
}
