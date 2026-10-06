package com.railnova.repository;

import com.railnova.entity.Coach;
import com.railnova.entity.CoachType;
import com.railnova.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    List<Coach> findByTrain(Train train);
    List<Coach> findByTrainId(Long trainId);
    List<Coach> findByTrainAndCoachType(Train train, CoachType coachType);
    Optional<Coach> findByTrainAndCoachCode(Train train, String coachCode);
}
