package com.railnova.service.impl;

import com.railnova.dto.UserDto;
import com.railnova.entity.SavedRoute;
import com.railnova.entity.Station;
import com.railnova.entity.User;
import com.railnova.exception.BadRequestException;
import com.railnova.exception.ResourceNotFoundException;
import com.railnova.repository.SavedRouteRepository;
import com.railnova.repository.StationRepository;
import com.railnova.repository.UserRepository;
import com.railnova.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final SavedRouteRepository savedRouteRepository;

    public UserServiceImpl(UserRepository userRepository,
                           StationRepository stationRepository,
                           SavedRouteRepository savedRouteRepository) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.savedRouteRepository = savedRouteRepository;
    }

    @Override
    public UserDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return new UserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserProfile(String email, String fullName, String phone) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        if (phone != null) {
            user.setPhone(phone.trim());
        }

        User updated = userRepository.save(user);
        return new UserDto(updated);
    }

    @Override
    public List<SavedRoute> getSavedRoutes(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return savedRouteRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional
    public SavedRoute saveRoute(String email, Long sourceStationId, Long destinationStationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Station source = stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new ResourceNotFoundException("Source station not found: " + sourceStationId));

        Station dest = stationRepository.findById(destinationStationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found: " + destinationStationId));

        if (savedRouteRepository.findByUserAndSourceStationAndDestinationStation(user, source, dest).isPresent()) {
            throw new BadRequestException("Route is already saved in your favourites.");
        }

        SavedRoute savedRoute = new SavedRoute(user, source, dest);
        return savedRouteRepository.save(savedRoute);
    }

    @Override
    @Transactional
    public void deleteSavedRoute(String email, Long savedRouteId) {
        SavedRoute sr = savedRouteRepository.findById(savedRouteId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved route not found: " + savedRouteId));

        if (!sr.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new BadRequestException("You can only remove your own saved routes.");
        }

        savedRouteRepository.delete(sr);
    }
}
