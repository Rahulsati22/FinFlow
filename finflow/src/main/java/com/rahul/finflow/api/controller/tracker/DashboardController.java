package com.rahul.finflow.api.controller.tracker;

import com.rahul.finflow.api.dto.tracker.DashboardSummaryResponse;
import com.rahul.finflow.core.service.tracker.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @RequestParam int month,
            @RequestParam int year,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DashboardSummaryResponse response = dashboardService.getDashboardSummary(month, year, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}