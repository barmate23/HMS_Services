package com.hotelerp.userservice.dto.hkdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HkAttentionItemDTO {
    private String label;
    private int count;
    private String type; // VACANT_DIRTY, MAINTENANCE_BLOCKER, LOST_FOUND
}
