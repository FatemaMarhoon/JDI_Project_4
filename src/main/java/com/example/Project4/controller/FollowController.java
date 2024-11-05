package com.example.Project4.controller;

import com.example.Project4.dao.GenericDao;
import com.example.Project4.model.Follow;
import com.example.Project4.model.Organization;
import com.example.Project4.model.User;
import com.example.Project4.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @GetMapping
    public List<Follow> getAllFollows() {
        return followService.getAllFollows();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Follow> getFollowById(@PathVariable Integer id) {
        return followService.getFollowById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GenericDao<Follow>> createFollow(@RequestBody Follow follow) {
        GenericDao<Follow> result = followService.createFollow(follow);

        if (!result.getErrors().isEmpty()) {
            // Return a bad request response if there are errors
            return ResponseEntity.badRequest().body(result);
        } else {
            // Return an OK response with the created follow if no errors
            return ResponseEntity.ok(result);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollow(@PathVariable Integer id) {
        try {
            followService.deleteFollow(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/follower/{followerId}")
    public List<Follow> getFollowsByFollower(@PathVariable User followerId) {
        return followService.getFollowsByFollower(followerId);
    }

    @GetMapping("/organization/{organizationId}")
    public List<Follow> getFollowsByFollowedOrganization(@PathVariable Organization organizationId) {
        return followService.getFollowsByFollowedOrganization(organizationId);
    }
}
