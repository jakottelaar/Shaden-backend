package com.example.shaden.features.statistics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.features.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<ResponseData> getAllStatistics() {
        ResponseData responseData = ResponseData.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Statistics retrieved successfully")
                .results(statisticsService.getAllStatistics())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

}
