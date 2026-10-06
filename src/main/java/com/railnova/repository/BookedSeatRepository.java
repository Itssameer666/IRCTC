package com.railnova.repository;

import com.railnova.entity.BookedSeat;
import com.railnova.entity.Coach;
import com.railnova.entity.Seat;
import com.railnova.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {

    @Query("SELECT bs.seat.id FROM BookedSeat bs WHERE bs.coach = :coach AND bs.journeyDate = :journeyDate AND bs.status IN ('RESERVED', 'BOOKED')")
    List<Long> findBookedSeatIdsByCoachAndDate(@Param("coach") Coach coach, @Param("journeyDate") LocalDate journeyDate);

    @Query("SELECT bs FROM BookedSeat bs WHERE bs.coach.id = :coachId AND bs.journeyDate = :journeyDate AND bs.status IN ('RESERVED', 'BOOKED')")
    List<BookedSeat> findByCoachIdAndJourneyDate(@Param("coachId") Long coachId, @Param("journeyDate") LocalDate journeyDate);

    boolean existsByTrainAndJourneyDateAndSeatAndStatusIn(Train train, LocalDate journeyDate, Seat seat, List<String> statuses);

    Optional<BookedSeat> findByTrainAndJourneyDateAndSeat(Train train, LocalDate journeyDate, Seat seat);

    void deleteByBookingId(Long bookingId);

    @Query("SELECT COUNT(bs) FROM BookedSeat bs WHERE bs.train.id = :trainId AND bs.journeyDate = :journeyDate AND bs.coach.coachType = com.railnova.entity.CoachType.SLEEPER AND bs.status = 'BOOKED'")
    long countBookedSeatsForTrainAndDate(@Param("trainId") Long trainId, @Param("journeyDate") LocalDate journeyDate);
}
