package com.railnova.repository;

import com.railnova.entity.SupportTicket;
import com.railnova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByUserOrderByCreatedAtDesc(User user);
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
