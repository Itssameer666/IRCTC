package com.railnova;

import com.railnova.dto.PnrResponseDto;
import com.railnova.dto.TrainSearchResultDto;
import com.railnova.service.PnrService;
import com.railnova.service.TrainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    private TrainService trainService;

    @Autowired
    private PnrService pnrService;

    @Test
    @DisplayName("Should successfully search trains between New Delhi and Varanasi")
    void testTrainSearch() {
        List<TrainSearchResultDto> trains = trainService.searchTrains(
                "NDLS", "BSB", LocalDate.now().plusDays(2), null, null, null, null, "lowest_fare"
        );
        assertNotNull(trains);
        assertFalse(trains.isEmpty(), "Expected to find at least one train between NDLS and BSB");
        assertEquals("22436", trains.get(0).getTrainNumber());
    }

    @Test
    @DisplayName("Should retrieve seeded PNR details")
    void testPnrEnquiry() {
        PnrResponseDto pnrDto = pnrService.getPnrStatus("8492019384");
        assertNotNull(pnrDto);
        assertEquals("8492019384", pnrDto.getPnrNumber());
        assertEquals("Vande Bharat Express", pnrDto.getTrainName());
        assertFalse(pnrDto.getPassengers().isEmpty());
    }
}
