package com.sangraj.carrental.service;
import com.sangraj.carrental.entity.ReviewEntity;
import com.sangraj.carrental.repository.ReviewSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserReviewService {
    @Autowired
    public final ReviewSaveRepository reviewSaveRepository;

    public UserReviewService(ReviewSaveRepository reviewSaveRepository) {
        this.reviewSaveRepository = reviewSaveRepository;

    }

    public ReviewEntity saveReview(ReviewEntity review) {
        review.setCreatedAt(LocalDateTime.now());
        return reviewSaveRepository.save(review);
    }
    public List<ReviewEntity> getAllReviews() {
        return reviewSaveRepository.findAll();
    }

}
