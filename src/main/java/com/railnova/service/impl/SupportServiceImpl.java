package com.railnova.service.impl;

import com.railnova.dto.SupportTicketRequestDto;
import com.railnova.dto.SupportTicketResponseDto;
import com.railnova.entity.SupportTicket;
import com.railnova.entity.User;
import com.railnova.exception.ResourceNotFoundException;
import com.railnova.repository.SupportTicketRepository;
import com.railnova.repository.UserRepository;
import com.railnova.service.NotificationService;
import com.railnova.service.SupportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportServiceImpl implements SupportService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public SupportServiceImpl(SupportTicketRepository supportTicketRepository,
                              UserRepository userRepository,
                              NotificationService notificationService) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public SupportTicketResponseDto createTicket(SupportTicketRequestDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        SupportTicket ticket = new SupportTicket(user, dto.getPnrNumber(), dto.getSubject(), dto.getMessage());
        SupportTicket savedTicket = supportTicketRepository.save(ticket);

        return new SupportTicketResponseDto(savedTicket);
    }

    @Override
    public List<SupportTicketResponseDto> getUserTickets(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        return supportTicketRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(SupportTicketResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportTicketResponseDto> getAllTickets() {
        return supportTicketRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(SupportTicketResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupportTicketResponseDto replyTicket(Long ticketId, String reply, String staffEmail) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        ticket.setStaffResponse(reply);
        ticket.setStatus("RESOLVED");
        SupportTicket updatedTicket = supportTicketRepository.save(ticket);

        notificationService.sendNotification(ticket.getUser(), "Support Ticket Update",
                "Your query regarding " + ticket.getSubject() + " has been resolved by our support staff.", "SYSTEM");

        return new SupportTicketResponseDto(updatedTicket);
    }
}
