package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.MinibarConsumptionDTO;
import com.hotelerp.userservice.service.MinibarConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/minibar")
@RequiredArgsConstructor
public class MinibarConsumptionController {

    private final MinibarConsumptionService minibarConsumptionService;

    @PostMapping("/post-consumption")
    public ResponseEntity<StandardResponse<MinibarConsumptionDTO>> postConsumption(@RequestBody MinibarConsumptionDTO dto) {
        return ResponseEntity.ok(minibarConsumptionService.postConsumption(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<MinibarConsumptionDTO>> updateConsumption(@PathVariable Long id, @RequestBody MinibarConsumptionDTO dto) {
        return ResponseEntity.ok(minibarConsumptionService.updateConsumption(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<MinibarConsumptionDTO>> getConsumptionById(@PathVariable Long id) {
        return ResponseEntity.ok(minibarConsumptionService.getConsumptionById(id));
    }

    @GetMapping
    public ResponseEntity<StandardResponse<List<MinibarConsumptionDTO>>> getAllConsumptions() {
        return ResponseEntity.ok(minibarConsumptionService.getAllConsumptions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteConsumption(@PathVariable Long id) {
        return ResponseEntity.ok(minibarConsumptionService.deleteConsumption(id));
    }
}
