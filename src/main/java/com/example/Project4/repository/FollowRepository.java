package com.example.Project4.repository;

import com.example.Project4.model.Follow;
import com.example.Project4.model.Organization;
import com.example.Project4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowedOrganization(Organization followedOrganization);
    boolean existsByFollowerAndFollowedOrganization(User follower, Organization followedOrganization);
}
