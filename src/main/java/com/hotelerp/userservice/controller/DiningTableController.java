package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.DiningTableDTO;
import com.hotelerp.userservice.dto.DiningTableWithoutOutletDTO;
import com.hotelerp.userservice.service.DiningTableService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/pos/tables")
@RequiredArgsConstructor
public class DiningTableController {

    private final DiningTableService tableService;

    @PostMapping(ServiceConstant.CREATE_TABLE)
    public ResponseEntity<StandardResponse<Void>> createTable(@RequestBody DiningTableDTO dto) {
        StandardResponse<Void> response = tableService.createTable(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_TABLE_BY_ID)
    public ResponseEntity<StandardResponse<DiningTableDTO>> getTableById(@PathVariable Long id) {
        StandardResponse<DiningTableDTO> response = tableService.getTableById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_TABLES)
    public ResponseEntity<StandardResponse<?>> getAllTables(@RequestParam(required = false) Long outletId) {
        if (outletId != null) {
            StandardResponse<List<DiningTableWithoutOutletDTO>> response = tableService.getTablesByOutlet(outletId);
            return ResponseEntity.ok(response);
        } else {
            StandardResponse<List<DiningTableDTO>> response = tableService.getAllTables();
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping(ServiceConstant.UPDATE_TABLE)
    public ResponseEntity<StandardResponse<DiningTableDTO>> updateTable(@PathVariable Long id, @RequestBody DiningTableDTO dto) {
        StandardResponse<DiningTableDTO> response = tableService.updateTable(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_TABLE)
    public ResponseEntity<StandardResponse<Void>> deleteTable(@PathVariable Long id) {
        StandardResponse<Void> response = tableService.deleteTable(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
