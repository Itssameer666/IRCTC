package com.railnova.repository;

import com.railnova.entity.Coach;
import com.railnova.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByCoachOrderBySeatNumberAsc(Coach coach);
    List<Seat> findByCoachIdOrderBySeatNumberAsc(Long coachId);
    Optional<Seat> findByCoachAndSeatNumber(Coach coach, Integer seatNumber);
}
