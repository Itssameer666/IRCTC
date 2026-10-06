package com.railnova.service;

import com.railnova.dto.AdminStatsDto;
import com.railnova.dto.UserDto;
import com.railnova.entity.AuditLog;
import com.railnova.entity.Refund;

import java.util.List;

public interface AdminService {
    AdminStatsDto getAdminStats();
    List<UserDto> getAllUsers();
    UserDto toggleUserStatus(Long userId);
    List<Refund> getAllRefunds();
    List<AuditLog> getAuditLogs();
}
