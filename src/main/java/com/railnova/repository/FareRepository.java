package com.railnova.repository;

import com.railnova.entity.CoachType;
import com.railnova.entity.Fare;
import com.railnova.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FareRepository extends JpaRepository<Fare, Long> {
    List<Fare> findByTrain(Train train);
    List<Fare> findByTrainId(Long trainId);
    Optional<Fare> findByTrainAndCoachType(Train train, CoachType coachType);
    Optional<Fare> findByTrainIdAndCoachType(Long trainId, CoachType coachType);
}
