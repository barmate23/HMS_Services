package com.hotelerp.userservice.dto.posdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KotQueueDTO {
    private String orderId;
    private String outletName;
    private String info; // e.g. "T01 - Arjun Menon"
    private int itemCount;
    private String status;
}
