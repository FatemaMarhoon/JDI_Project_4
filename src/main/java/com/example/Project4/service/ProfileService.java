package com.example.Project4.service;


import com.example.Project4.dao.GenericDao;
import com.example.Project4.dto.ProfileDto;
import com.example.Project4.model.UserProfile;
import com.example.Project4.repository.ProfileRepository;
import com.example.Project4.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final StorageService storageService;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, StorageService storageService) {
        this.profileRepository = profileRepository;
        this.storageService = storageService;
    }

    public ProfileDto getById(Long profileId) {
        return profileRepository.findById(profileId)
                .map(profile -> new ProfileDto(profile, false))  // Provide both parameters for the constructor
                .orElse(null);
    }

    public List<ProfileDto> getAll() {
        return profileRepository.findAll().stream()
                .map(profile -> new ProfileDto(profile, false))  // Provide the required parameters
                .toList();
    }

    public GenericDao<ProfileDto> editProfile(ProfileDto dto, MultipartFile file) {
        GenericDao<ProfileDto> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (dto.getId() == null) {
            errors.add("Profile ID cannot be empty");
        }

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            errors.add("First name cannot be empty");
        }

        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            errors.add("Last name cannot be empty");
        }

        if (!Objects.equals(dto.getId(), UserService.getCurrentLoggedInUser().getId())) {
            errors.add("You cannot edit a profile of another user");
        }

        if (errors.isEmpty()) {
            Optional<UserProfile> retrievedProfile = profileRepository.findById(dto.getId());

            if (retrievedProfile.isPresent()) {
                if (retrievedProfile.get().getUser().getId().equals(UserService.getCurrentLoggedInUser().getId())) {
                    UserProfile profile = retrievedProfile.get();
                    profile.setFirstName(dto.getFirstName());
                    profile.setLastName(dto.getLastName());

                    if (file != null && !file.isEmpty()) {
                        String filename = storageService.store(file);
                        String fileUrl = "upload-dir/" + filename;
                        profile.setProfilePic(fileUrl);
                    }

                    UserProfile savedProfile = profileRepository.save(profile);
                    returnDao.setObject(new ProfileDto(savedProfile, false));
                } else {
                    errors.add("Cannot edit profile of another user");
                }
            } else {
                errors.add("Profile does not exist");
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Boolean> updateProfilePic(Long profileId, String profilePicUrl) {
        GenericDao<Boolean> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (profileId == null) {
            errors.add("Profile ID cannot be empty");
        }

        if (errors.isEmpty()) {
            Optional<UserProfile> retrievedProfile = profileRepository.findById(profileId);

            if (retrievedProfile.isPresent()) {
                if (retrievedProfile.get().getUser().getId().equals(UserService.getCurrentLoggedInUser().getId())) {
                    retrievedProfile.get().setProfilePic(profilePicUrl);
                    profileRepository.save(retrievedProfile.get());
                    returnDao.setObject(true);
                } else {
                    errors.add("Cannot update profile picture of another user profile");
                }
            } else {
                errors.add("Profile does not exist");
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
            returnDao.setObject(false);
        }

        return returnDao;
    }


}
