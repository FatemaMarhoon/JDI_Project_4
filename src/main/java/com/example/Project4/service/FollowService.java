package com.example.Project4.service;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.Application;
import com.example.Project4.model.Follow;
import com.example.Project4.model.Organization;
import com.example.Project4.model.User;
import com.example.Project4.repository.FollowRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;

    public FollowService(FollowRepository followRepository, UserService userService) {
        this.followRepository = followRepository;
        this.userService = userService;
    }

    @Autowired


    public List<Follow> getAllFollows() {
        return followRepository.findAll();
    }

    public Optional<Follow> getFollowById(Integer id) {
        return followRepository.findById(id);
    }

    public List<Follow> getFollowsByFollower(User follower) {
        return followRepository.findByFollower(follower);
    }

    public List<Follow> getFollowsByFollowedOrganization(Organization organization) {
        return followRepository.findByFollowedOrganization(organization);
    }

    @Transactional
    public GenericDao<Follow> createFollow(Follow follow) {
        GenericDao<Follow> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        follow.setFollower(userService.getCurrentUser());

        boolean exists = followRepository.existsByFollowerAndFollowedOrganization(
                follow.getFollower(), follow.getFollowedOrganization());

        if (exists) {
            errors.add("User is already following this organization.");
        } else {
            follow.setFollowedAt(LocalDateTime.now());
            Follow savedFollow = followRepository.save(follow);
            returnDao.setObject(savedFollow);
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }




    public void deleteFollow(Integer id) {
        Follow follow = followRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Follow not found with id " + id));
        followRepository.delete(follow);
    }
}
