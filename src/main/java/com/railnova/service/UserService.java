package com.railnova.service;

import com.railnova.dto.UserDto;
import com.railnova.entity.SavedRoute;

import java.util.List;

public interface UserService {
    UserDto getUserProfile(String email);
    UserDto updateUserProfile(String email, String fullName, String phone);
    List<SavedRoute> getSavedRoutes(String email);
    SavedRoute saveRoute(String email, Long sourceStationId, Long destinationStationId);
    void deleteSavedRoute(String email, Long savedRouteId);
}
