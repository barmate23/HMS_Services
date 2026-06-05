package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
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
    public ResponseEntity<StandardResponse<Void>> createOutlet(@RequestBody OutletDTO dto) {
        StandardResponse<Void> response = outletService.createOutlet(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_OUTLET_BY_ID)
    public ResponseEntity<StandardResponse<OutletDTO>> getOutletById(@PathVariable Long id) {
        StandardResponse<OutletDTO> response = outletService.getOutletById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_OUTLETS)
    public ResponseEntity<StandardResponse<List<OutletDTO>>> getAllOutlets() {
        StandardResponse<List<OutletDTO>> response = outletService.getAllOutlets();
        return ResponseEntity.ok(response);
    }

    @PutMapping(ServiceConstant.UPDATE_OUTLET)
    public ResponseEntity<StandardResponse<OutletDTO>> updateOutlet(@PathVariable Long id, @RequestBody OutletDTO dto) {
        StandardResponse<OutletDTO> response = outletService.updateOutlet(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_OUTLET)
    public ResponseEntity<StandardResponse<Void>> deleteOutlet(@PathVariable Long id) {
        StandardResponse<Void> response = outletService.deleteOutlet(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
