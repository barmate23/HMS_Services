package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.dto.LostAndFoundDTO;
import com.hotelerp.userservice.service.LostAndFoundService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lost-found")
@RequiredArgsConstructor
public class LostAndFoundController {

    private final LostAndFoundService lostAndFoundService;

    @PostMapping(ServiceConstant.CREATE_LOST_FOUND_ITEM)
    public ResponseEntity<LostAndFoundDTO> logItem(@RequestBody LostAndFoundDTO dto) {
        return new ResponseEntity<>(lostAndFoundService.logItem(dto), HttpStatus.CREATED);
    }

    @GetMapping(ServiceConstant.GET_LOST_ITEM_BY_ID)
    public ResponseEntity<LostAndFoundDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(lostAndFoundService.getItemById(id));
    }

    @GetMapping(ServiceConstant.GET_ALL_LOST_ITEMS)
    public ResponseEntity<List<LostAndFoundDTO>> getAllItems() {
        return ResponseEntity.ok(lostAndFoundService.getAllItems());
    }

    @PutMapping(ServiceConstant.UPDATE_LOST_FOUND_ITEM)
    public ResponseEntity<LostAndFoundDTO> updateItem(@PathVariable Long id, @RequestBody LostAndFoundDTO dto) {
        return ResponseEntity.ok(lostAndFoundService.updateItem(id, dto));
    }

    @DeleteMapping(ServiceConstant.DELETE_LOST_FOUND_ITEM)
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        lostAndFoundService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(ServiceConstant.UPDATE_LOST_FOUND_ITEM_STATUS)
    public ResponseEntity<LostAndFoundDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(lostAndFoundService.updateStatus(id, status));
    }
}
