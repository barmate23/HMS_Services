package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreIssueDTO {
    private Long id;
    private String issueNumber;

    private Long departmentId;
    private String departmentName;

    private String issuedTo;

    private Long itemId;
    private String itemName;
    private String itemCode;

    private Integer quantity;
    private String issueNote;
    private LocalDate issueDate;

    private Long statusId;
    private String statusName;
    private String statusCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
