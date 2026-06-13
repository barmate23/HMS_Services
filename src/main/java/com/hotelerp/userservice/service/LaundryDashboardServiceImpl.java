package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.LaundryOrderDTO;
import com.hotelerp.userservice.dto.laundrydashboard.LaundryDashboardActivityDTO;
import com.hotelerp.userservice.dto.laundrydashboard.LaundryDashboardDTO;
import com.hotelerp.userservice.dto.laundrydashboard.LaundryDashboardSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LaundryDashboardServiceImpl implements LaundryDashboardService {

    private final LaundryService laundryService;

    @Override
    public StandardResponse<LaundryDashboardDTO> getLaundryDashboardData() {
        try {
            List<LaundryOrderDTO> orders = laundryService.getAllLaundryOrders().getData();
            List<LaundryOrderDTO> safeOrders = orders != null ? orders : List.of();
            LocalDate today = LocalDate.now();

            LaundryDashboardSummaryDTO summary = LaundryDashboardSummaryDTO.builder()
                    .pendingPickup(countStatus(safeOrders, "PENDING", "PICKUP_PENDING", "PICKUP PENDING"))
                    .inProcess(countStatus(safeOrders, "PROCESSING", "IN_PROCESS", "IN PROCESS"))
                    .ready(countStatus(safeOrders, "READY", "READY_FOR_DELIVERY", "READY FOR DELIVERY"))
                    .revenue(safeOrders.stream()
                            .filter(order -> isCreatedToday(order, today))
                            .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount() : 0D)
                            .sum())
                    .overdue(countStatus(safeOrders, "OVERDUE"))
                    .completedToday((int) safeOrders.stream()
                            .filter(order -> isCreatedToday(order, today))
                            .filter(order -> hasStatus(order.getStatus(), "DELIVERED", "COMPLETED"))
                            .count())
                    .build();

            List<LaundryDashboardActivityDTO> activityFeed = safeOrders.stream()
                    .sorted(Comparator.comparing(LaundryOrderDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(10)
                    .map(LaundryDashboardActivityDTO::fromOrder)
                    .collect(Collectors.toList());

            LaundryDashboardDTO dashboardDTO = LaundryDashboardDTO.builder()
                    .summary(summary)
                    .activityFeed(activityFeed)
                    .build();

            return StandardResponse.success(dashboardDTO, "Laundry dashboard data fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching laundry dashboard data: ", e);
            return StandardResponse.error("Failed to fetch laundry dashboard data", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private int countStatus(List<LaundryOrderDTO> orders, String... expectedValues) {
        return (int) orders.stream()
                .filter(order -> hasStatus(order.getStatus(), expectedValues))
                .count();
    }

    private boolean hasStatus(String status, String... expectedValues) {
        String normalizedStatus = normalizeStatus(status);
        for (String expected : expectedValues) {
            if (normalizedStatus.equals(normalizeStatus(expected))) {
                return true;
            }
        }
        return false;
    }

    private boolean isCreatedToday(LaundryOrderDTO order, LocalDate today) {
        return order.getCreatedAt() != null && order.getCreatedAt().toLocalDate().equals(today);
    }

    private String normalizeStatus(String status) {
        return String.valueOf(status)
                .trim()
                .replace('-', ' ')
                .replace('_', ' ')
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }
}
