package com.railnova.repository;

import com.railnova.entity.Station;
import com.railnova.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findByTrainNumber(String trainNumber);

    List<Train> findByActiveTrue();

    @Query("SELECT DISTINCT t FROM Train t " +
           "WHERE t.active = true " +
           "AND ((t.sourceStation = :source AND t.destinationStation = :destination) " +
           "     OR (EXISTS (SELECT r1 FROM TrainRoute r1 WHERE r1.train = t AND r1.station = :source) " +
           "         AND EXISTS (SELECT r2 FROM TrainRoute r2 WHERE r2.train = t AND r2.station = :destination " +
           "                     AND r2.stopNumber > (SELECT r1.stopNumber FROM TrainRoute r1 WHERE r1.train = t AND r1.station = :source))))")
    List<Train> findTrainsBetweenStations(@Param("source") Station source, @Param("destination") Station destination);

    long countByActiveTrue();
}
