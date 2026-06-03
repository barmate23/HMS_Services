package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.dto.DiningTableDTO;
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
    public ResponseEntity<DiningTableDTO> createTable(@RequestBody DiningTableDTO dto) {
        return new ResponseEntity<>(tableService.createTable(dto), HttpStatus.CREATED);
    }

    @GetMapping(ServiceConstant.GET_TABLE_BY_ID)
    public ResponseEntity<DiningTableDTO> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }

    @GetMapping(ServiceConstant.GET_ALL_TABLES)
    public ResponseEntity<List<DiningTableDTO>> getAllTables(@RequestParam(required = false) Long outletId) {
        if (outletId != null) {
            return ResponseEntity.ok(tableService.getTablesByOutlet(outletId));
        }
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @PutMapping(ServiceConstant.UPDATE_TABLE)
    public ResponseEntity<DiningTableDTO> updateTable(@PathVariable Long id, @RequestBody DiningTableDTO dto) {
        return ResponseEntity.ok(tableService.updateTable(id, dto));
    }

    @DeleteMapping(ServiceConstant.DELETE_TABLE)
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
