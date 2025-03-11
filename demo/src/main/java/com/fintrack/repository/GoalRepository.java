package com.fintrack.repository;

import com.fintrack.entity.Goal;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GoalRepository extends MongoRepository<Goal, String> {
    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $lookup: { from: 'users', localField: 'userId', foreignField: '_id', as: 'userDetails' } }",
            "{ $unwind: '$userDetails' }",
            "{ $project: { 'userDetails.email': 1 } }"
    })
    String findUserEmailByGoalId(String goalId);
    Optional<Goal> findById(String goalId);
}
