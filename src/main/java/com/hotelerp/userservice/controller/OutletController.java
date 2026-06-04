package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.dto.OutletDTO;
import com.hotelerp.userservice.service.OutletService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/pos/outlets")
@RequiredArgsConstructor
public class OutletController {

    private final OutletService outletService;

    @PostMapping(ServiceConstant.CREATE_OUTLET)
    public ResponseEntity<Void> createOutlet(@RequestBody OutletDTO dto) {
        outletService.createOutlet(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(ServiceConstant.GET_OUTLET_BY_ID)
    public ResponseEntity<OutletDTO> getOutletById(@PathVariable Long id) {
        return ResponseEntity.ok(outletService.getOutletById(id));
    }

    @GetMapping(ServiceConstant.GET_ALL_OUTLETS)
    public ResponseEntity<List<OutletDTO>> getAllOutlets() {
        return ResponseEntity.ok(outletService.getAllOutlets());
    }

    @PutMapping(ServiceConstant.UPDATE_OUTLET)
    public ResponseEntity<OutletDTO> updateOutlet(@PathVariable Long id, @RequestBody OutletDTO dto) {
        return ResponseEntity.ok(outletService.updateOutlet(id, dto));
    }

    @DeleteMapping(ServiceConstant.DELETE_OUTLET)
    public ResponseEntity<Void> deleteOutlet(@PathVariable Long id) {
        outletService.deleteOutlet(id);
        return ResponseEntity.noContent().build();
    }
}
