package com.hotelerp.userservice.dto;

import com.hotelerp.common.entity.LostAndFoundItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostAndFoundDTO {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String itemDescription;
    private Long categoryId;
    private String categoryValue;
    private Long foundById;
    private String foundByName;
    private String storageLocation;
    private LocalDate foundDate;
    private String guestName;
    private String guestContact;
    private String storageNotes;
    private LostAndFoundItem.ItemStatus status;
}
