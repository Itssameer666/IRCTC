package com.railnova.repository;

import com.railnova.entity.Train;
import com.railnova.entity.TrainRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainRouteRepository extends JpaRepository<TrainRoute, Long> {
    List<TrainRoute> findByTrainOrderByStopNumberAsc(Train train);
    List<TrainRoute> findByTrainIdOrderByStopNumberAsc(Long trainId);
}
