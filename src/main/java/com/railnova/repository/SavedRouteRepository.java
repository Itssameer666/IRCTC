package com.railnova.repository;

import com.railnova.entity.SavedRoute;
import com.railnova.entity.Station;
import com.railnova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedRouteRepository extends JpaRepository<SavedRoute, Long> {
    List<SavedRoute> findByUserOrderByCreatedAtDesc(User user);
    List<SavedRoute> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<SavedRoute> findByUserAndSourceStationAndDestinationStation(User user, Station source, Station destination);
}
