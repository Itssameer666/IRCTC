package com.railnova.service;

import com.railnova.dto.SupportTicketRequestDto;
import com.railnova.dto.SupportTicketResponseDto;

import java.util.List;

public interface SupportService {
    SupportTicketResponseDto createTicket(SupportTicketRequestDto dto, String userEmail);
    List<SupportTicketResponseDto> getUserTickets(String userEmail);
    List<SupportTicketResponseDto> getAllTickets();
    SupportTicketResponseDto replyTicket(Long ticketId, String reply, String staffEmail);
}
