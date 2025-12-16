package com.sangraj.carrental.controller;

import com.sangraj.carrental.entity.ReviewEntity;
import com.sangraj.carrental.service.UserReviewService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Reviews")
public class ReviewController {
    @Autowired
    public UserReviewService userReviewService;

    @PostMapping
    public ResponseEntity<ReviewEntity> addReview(
            @RequestBody ReviewEntity review,
            Authentication authentication
    ) {
        // Username extracted from JWT
        String username = authentication.name();
        review.setUsername(username);

        ReviewEntity savedReview = userReviewService.saveReview(review);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<ReviewEntity>> getALLReview(){
        return ResponseEntity.ok(
                userReviewService.getAllReviews()
        );
    }


}
