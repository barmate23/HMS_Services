package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.CommonMasterDTO;
import com.hotelerp.userservice.service.CommonMasterService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/common")
@RequiredArgsConstructor
public class CommonMasterController {

    private final CommonMasterService commonMasterService;

    @PostMapping(ServiceConstant.SAVE_COMMON_MASTER)
    public ResponseEntity<StandardResponse<CommonMasterDTO>> saveCommonMaster(@RequestBody CommonMasterDTO dto) {
        StandardResponse<CommonMasterDTO> response = commonMasterService.saveCommonMaster(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping(ServiceConstant.UPDATE_COMMON_MASTER_DATA)
    public ResponseEntity<StandardResponse<CommonMasterDTO>> updateCommonMasterData(@RequestBody CommonMasterDTO dto) {
        StandardResponse<CommonMasterDTO> response = commonMasterService.updateCommonMasterData(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_COMMON_MASTER)
    public ResponseEntity<StandardResponse<List<CommonMasterDTO>>> getMastersByCategory(@PathVariable String category) {
        StandardResponse<List<CommonMasterDTO>> response = commonMasterService.getMastersByCategory(category);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteCommonMaster/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteCommonMaster(@PathVariable Long id) {
        StandardResponse<Void> response = commonMasterService.deleteCommonMaster(id);
        return ResponseEntity.ok(response);
    }
}
