package com.railnova.dto;

import jakarta.validation.constraints.NotBlank;

public class SupportTicketRequestDto {

    private String pnrNumber;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message is required")
    private String message;

    public SupportTicketRequestDto() {
    }

    public SupportTicketRequestDto(String pnrNumber, String subject, String message) {
        this.pnrNumber = pnrNumber;
        this.subject = subject;
        this.message = message;
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
}
