package com.example.Project4.controller;


import com.example.Project4.dao.GenericDao;
import com.example.Project4.dto.ProfileDto;
import com.example.Project4.service.ProfileService;
import com.example.Project4.service.UserService;
import com.example.Project4.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "http://localhost:3000")

public class ProfileController {

    private final ProfileService profileService;
    private final StorageService storageService;
    private final UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, StorageService storageService, UserService userService) {
        this.profileService = profileService;
        this.storageService = storageService;
        this.userService = userService;
    }

    @PutMapping
    public ResponseEntity<GenericDao<ProfileDto>> editProfile(@RequestPart("profile") ProfileDto dto,
                                                              @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            GenericDao<ProfileDto> genericDao = profileService.editProfile(dto, file);
            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.OK) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println("Exception caught and log saved."+e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<GenericDao<ProfileDto>> getProfileById(@PathVariable Long profileId) {
        try {
            ProfileDto dto = profileService.getById(profileId);
            return dto != null ?
                    new ResponseEntity<>(new GenericDao<>(dto, null), HttpStatus.OK) :
                    new ResponseEntity<>(new GenericDao<>(null, List.of("Profile with the id " + profileId + " not found")), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<GenericDao<List<ProfileDto>>> getAllProfiles() {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                List<ProfileDto> dtos = profileService.getAll();
                return !dtos.isEmpty() ?
                        new ResponseEntity<>(new GenericDao<>(dtos, null), HttpStatus.OK) :
                        new ResponseEntity<>(new GenericDao<>(null, List.of("No profiles found")), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/uploadProfilePic")
    public ResponseEntity<?> handleProfilePicUpload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Store the file and get its filename
            String filename = storageService.store(file);

            // Format the file path as 'upload-dir/filename'
            String fileUrl = "upload-dir/" + filename;

            // Update the profile with the new profile picture URL
            GenericDao<Boolean> result = profileService.updateProfilePic(id, fileUrl);

            // Check for errors and respond accordingly
            if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getErrors());
            }

            // Return a success response
            return ResponseEntity.status(HttpStatus.OK).body("Profile picture updated successfully");
        } catch (Exception e) {
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource file = storageService.loadAsResource(filename);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception e) {
            System.out.println("Exception caught and log saved."+e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
