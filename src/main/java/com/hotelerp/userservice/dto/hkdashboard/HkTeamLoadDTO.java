package com.hotelerp.userservice.dto.hkdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HkTeamLoadDTO {
    private int pendingSubmissions;
    private int inProgress;
    private int staffProfiles;
}
