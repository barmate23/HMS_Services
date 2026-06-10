package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.CommonMasterDTO;
import java.util.List;

public interface CommonMasterService {
    StandardResponse<CommonMasterDTO> saveCommonMaster(CommonMasterDTO dto);
    StandardResponse<List<CommonMasterDTO>> getMastersByCategory(String category);
    StandardResponse<Void> deleteCommonMaster(Long id);
}
