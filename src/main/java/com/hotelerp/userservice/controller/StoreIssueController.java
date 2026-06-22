package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.StoreIssueDTO;
import com.hotelerp.userservice.service.StoreIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/inventory/store-issues")
@RequiredArgsConstructor
public class StoreIssueController {

    private final StoreIssueService storeIssueService;

    @PostMapping("/createStoreIssue")
    public ResponseEntity<StandardResponse<StoreIssueDTO>> create(@RequestBody StoreIssueDTO dto) {
        return ResponseEntity.ok(storeIssueService.createStoreIssue(dto));
    }

    @PutMapping("/updateStoreIssue/{id}")
    public ResponseEntity<StandardResponse<StoreIssueDTO>> update(@PathVariable Long id, @RequestBody StoreIssueDTO dto) {
        return ResponseEntity.ok(storeIssueService.updateStoreIssue(id, dto));
    }

    @GetMapping("/getByStoreIssueId/{id}")
    public ResponseEntity<StandardResponse<StoreIssueDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storeIssueService.getStoreIssueById(id));
    }

    @GetMapping("/getAllStoreIssue")
    public ResponseEntity<StandardResponse<List<StoreIssueDTO>>> getAll() {
        return ResponseEntity.ok(storeIssueService.getAllStoreIssues());
    }

    @DeleteMapping("/deleteStoreIssue/{id}")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(storeIssueService.deleteStoreIssue(id));
    }

    @PostMapping("/updateStoreIssueStatus")
    public ResponseEntity<StandardResponse<StoreIssueDTO>> updateStatus(@RequestParam Long id, @RequestParam Long statusId) {
        return ResponseEntity.ok(storeIssueService.updateStatus(id, statusId));
    }
}
