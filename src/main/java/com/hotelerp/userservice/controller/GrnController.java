package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.GrnDTO;
import com.hotelerp.userservice.service.GrnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/purchase/grn")
@RequiredArgsConstructor
public class GrnController {

    private final GrnService grnService;

    @PostMapping("/createGrn")
    public ResponseEntity<StandardResponse<GrnDTO>> create(@RequestBody GrnDTO dto) {
        return ResponseEntity.ok(grnService.createGrn(dto));
    }

    @PutMapping("updateGrn/{id}")
    public ResponseEntity<StandardResponse<GrnDTO>> update(@PathVariable Long id, @RequestBody GrnDTO dto) {
        return ResponseEntity.ok(grnService.updateGrn(id, dto));
    }

    @GetMapping("getGrnById/{id}")
    public ResponseEntity<StandardResponse<GrnDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(grnService.getGrnById(id));
    }

    @GetMapping("/getAllGrn")
    public ResponseEntity<StandardResponse<List<GrnDTO>>> getAll() {
        return ResponseEntity.ok(grnService.getAllGrns());
    }

    @DeleteMapping("/deleteGrn/{id}")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(grnService.deleteGrn(id));
    }
}
