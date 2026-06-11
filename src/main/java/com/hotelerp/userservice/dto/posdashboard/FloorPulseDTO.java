package com.hotelerp.userservice.dto.posdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorPulseDTO {
    private int totalTables;
    private int occupied;
    private int available;
    private int reserved;
    private double occupiedPercent;
    private double availablePercent;
    private double reservedPercent;
}
