package com.railnova.dto;

import com.railnova.entity.SupportTicket;
import java.time.LocalDateTime;

public class SupportTicketResponseDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private String pnrNumber;
    private String subject;
    private String message;
    private String status;
    private String staffResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupportTicketResponseDto() {
    }

    public SupportTicketResponseDto(SupportTicket st) {
        if (st != null) {
            this.id = st.getId();
            if (st.getUser() != null) {
                this.userId = st.getUser().getId();
                this.userEmail = st.getUser().getEmail();
                this.userName = st.getUser().getFullName();
            }
            this.pnrNumber = st.getPnrNumber();
            this.subject = st.getSubject();
            this.message = st.getMessage();
            this.status = st.getStatus();
            this.staffResponse = st.getStaffResponse();
            this.createdAt = st.getCreatedAt();
            this.updatedAt = st.getUpdatedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStaffResponse() {
        return staffResponse;
    }

    public void setStaffResponse(String staffResponse) {
        this.staffResponse = staffResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
