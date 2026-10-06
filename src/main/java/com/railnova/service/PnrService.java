package com.railnova.service;

import com.railnova.dto.PnrResponseDto;

public interface PnrService {
    PnrResponseDto getPnrStatus(String pnr);
}
