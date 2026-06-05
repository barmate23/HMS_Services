package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.LostAndFoundDTO;
import com.hotelerp.userservice.service.LostAndFoundService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/lost-found")
@RequiredArgsConstructor
public class LostAndFoundController {

    private final LostAndFoundService lostAndFoundService;

    @PostMapping(ServiceConstant.CREATE_LOST_FOUND_ITEM)
    public ResponseEntity<StandardResponse<Void>> logItem(@RequestBody LostAndFoundDTO dto) {
        StandardResponse<Void> response = lostAndFoundService.logItem(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_LOST_ITEM_BY_ID)
    public ResponseEntity<StandardResponse<LostAndFoundDTO>> getItemById(@PathVariable Long id) {
        StandardResponse<LostAndFoundDTO> response = lostAndFoundService.getItemById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_LOST_ITEMS)
    public ResponseEntity<StandardResponse<List<LostAndFoundDTO>>> getAllItems() {
        StandardResponse<List<LostAndFoundDTO>> response = lostAndFoundService.getAllItems();
        return ResponseEntity.ok(response);
    }

    @PutMapping(ServiceConstant.UPDATE_LOST_FOUND_ITEM)
    public ResponseEntity<StandardResponse<LostAndFoundDTO>> updateItem(@PathVariable Long id, @RequestBody LostAndFoundDTO dto) {
        StandardResponse<LostAndFoundDTO> response = lostAndFoundService.updateItem(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_LOST_FOUND_ITEM)
    public ResponseEntity<StandardResponse<Void>> deleteItem(@PathVariable Long id) {
        StandardResponse<Void> response = lostAndFoundService.deleteItem(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping(ServiceConstant.UPDATE_LOST_FOUND_ITEM_STATUS)
    public ResponseEntity<StandardResponse<LostAndFoundDTO>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        StandardResponse<LostAndFoundDTO> response = lostAndFoundService.updateStatus(id, status);
        HttpStatus httpStatus = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(httpStatus).body(response);
    }
}
