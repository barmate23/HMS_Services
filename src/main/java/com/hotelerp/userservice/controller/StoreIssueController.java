package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.StoreIssueDTO;
import com.hotelerp.userservice.service.StoreIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/store-issues")
@RequiredArgsConstructor
public class StoreIssueController {

    private final StoreIssueService storeIssueService;

    @PostMapping
    public ResponseEntity<StandardResponse<StoreIssueDTO>> create(@RequestBody StoreIssueDTO dto) {
        return ResponseEntity.ok(storeIssueService.createStoreIssue(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<StoreIssueDTO>> update(@PathVariable Long id, @RequestBody StoreIssueDTO dto) {
        return ResponseEntity.ok(storeIssueService.updateStoreIssue(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<StoreIssueDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storeIssueService.getStoreIssueById(id));
    }

    @GetMapping
    public ResponseEntity<StandardResponse<List<StoreIssueDTO>>> getAll() {
        return ResponseEntity.ok(storeIssueService.getAllStoreIssues());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(storeIssueService.deleteStoreIssue(id));
    }

    @PatchMapping("/{id}/status/{statusId}")
    public ResponseEntity<StandardResponse<StoreIssueDTO>> updateStatus(@PathVariable Long id, @PathVariable Long statusId) {
        return ResponseEntity.ok(storeIssueService.updateStatus(id, statusId));
    }
}
