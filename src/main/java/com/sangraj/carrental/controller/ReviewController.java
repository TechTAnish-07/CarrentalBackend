package com.sangraj.carrental.controller;

import com.sangraj.carrental.dto.ReviewRequest;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.ReviewEntity;
import com.sangraj.carrental.repository.ReviewSaveRepository;
import com.sangraj.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewSaveRepository reviewRepo;
    private final UserRepository userRepository;
    @PostMapping
    public ResponseEntity<?> addReview(
            @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String username =
                (request.name() != null && !request.name().isBlank())
                        ? request.name()
                        : user.getDisplayName();   // ✅ FIX HERE

        ReviewEntity review = new ReviewEntity();
        review.setComment(request.comment());
        review.setRating(request.rating());
        review.setUsername(username);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepo.save(review);

        return ResponseEntity.ok("Review added successfully");
    }


    @GetMapping
    public List<ReviewEntity> getReviews(Authentication authentication) {
        return reviewRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
