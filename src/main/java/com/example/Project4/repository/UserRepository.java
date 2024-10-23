package com.example.Project4.repository;


import com.example.Project4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByStatus(Boolean status);

    //to login, return user object based on email address
    Optional<User> findUserByEmail(String emailAddress);
    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    Optional<User> findByVerificationCode(String code);
    Optional<User> findByResetToken(String resetToken);

}

