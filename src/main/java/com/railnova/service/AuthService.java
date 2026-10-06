package com.railnova.service;

import com.railnova.dto.*;
import com.railnova.entity.User;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    User getCurrentAuthenticatedUser();
}
