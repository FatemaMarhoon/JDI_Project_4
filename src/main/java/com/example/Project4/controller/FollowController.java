package com.example.Project4.controller;

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
    public ResponseEntity<Follow> createFollow(@RequestBody Follow follow) {
        try {
            Follow createdFollow = followService.createFollow(follow);
            return ResponseEntity.ok(createdFollow);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // handle duplicate follow case
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
