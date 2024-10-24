package com.example.Project4.controller;


import com.example.Project4.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/files")
public class FileController {

    private final S3Service s3Service;

    @Autowired
    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Get paginated file URLs stored in the S3 bucket.
     *
     * @param page The page number (starting from 0).
     * @param size The number of files to return per page.
     * @return CompletableFuture containing a list of file URLs.
     */
    @GetMapping
    public CompletableFuture<ResponseEntity<List<String>>> getAllFiles(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        return s3Service.getPaginatedFileUrls(page, size)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).body(List.of("Failed to retrieve file list " + e.getMessage())));
    }



    /**
     * Endpoint to retrieve the CloudFront URL for a file stored in S3.
     *
     * @param fileKey The key (name) of the file in S3.
     * @return A ResponseEntity with the CloudFront URL for the file.
     */
    @GetMapping("/{fileKey}")
    public CompletableFuture<ResponseEntity<String>> retrieveFile(@PathVariable String fileKey) {
        return s3Service.doesFileExist(fileKey)
                .thenApply(fileExists -> {
                    if (!fileExists) {
                        return new ResponseEntity<>("File not found: " + fileKey, HttpStatus.NOT_FOUND);
                    }
                    // File exists, return the CDN URL
                    String cdnUrl = s3Service.getCDNFileUrl(fileKey);
                    return new ResponseEntity<>(cdnUrl, HttpStatus.OK);
                })
                .exceptionally(e -> new ResponseEntity<>("Failed to retrieve file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * Endpoint to upload multiple files to S3.
     *
     * @param files The array of files to upload.
     * @return A ResponseEntity with a list of S3 URLs of the uploaded files.
     */
    @PostMapping()
    public CompletableFuture<ResponseEntity<List<String>>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<CompletableFuture<String>> uploadFutures = new ArrayList<>();

        for (MultipartFile file : files) {
            CompletableFuture<String> uploadFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    // Upload file and return the URL
                    return s3Service.uploadFile(file.getBytes(), file.getOriginalFilename(), file.getContentType()).join();
                } catch (Exception e) {
                    throw new RuntimeException( e.getMessage());
                }
            });
            uploadFutures.add(uploadFuture);
        }

        return CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<String> fileUrls = new ArrayList<>();
                    uploadFutures.forEach(future -> fileUrls.add(future.join()));
                    return new ResponseEntity<>(fileUrls, HttpStatus.OK);
                })
                .exceptionally(ex -> new ResponseEntity<>(List.of("Failed to upload one or more files "+ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * Endpoint to delete a file from the S3 bucket.
     *
     * @param fileKey The key (name) of the file to be deleted.
     * @return ResponseEntity indicating the status of the deletion operation.
     */
    @DeleteMapping("/{fileKey}")
    public CompletableFuture<ResponseEntity<String>> deleteFile(@PathVariable String fileKey) {
        return s3Service.doesFileExist(fileKey)
                .thenCompose(fileExists -> {
                    if (!fileExists) {
                        // If the file does not exist, return 404 Not Found
                        return CompletableFuture.completedFuture(
                                new ResponseEntity<>("File not found: " + fileKey, HttpStatus.NOT_FOUND));
                    }
                    // If the file exists, proceed to delete
                    return s3Service.deleteFile(fileKey)
                            .thenApply(result -> ResponseEntity.ok().body("File deleted successfully."))
                            .exceptionally(e -> ResponseEntity.internalServerError().body("Failed to delete file: " + e.getMessage()));
                });
    }

}