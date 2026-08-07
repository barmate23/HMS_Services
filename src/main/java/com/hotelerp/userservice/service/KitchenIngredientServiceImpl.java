package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.KitchenIngredientPageResponse;
import com.hotelerp.userservice.dto.KitchenIngredientRequestDTO;
import com.hotelerp.userservice.dto.KitchenIngredientResponseDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.KitchenIngredient;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.KitchenIngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KitchenIngredientServiceImpl implements KitchenIngredientService {

    private final KitchenIngredientRepository ingredientRepository;
    private final CommonMasterRepository commonMasterRepository;

    // ──────────────────────────────────────────────────────────────────────
    //  CREATE
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<Void> createIngredient(KitchenIngredientRequestDTO dto) {
        try {
            if (dto.getIngredientName() == null || dto.getIngredientName().isBlank()) {
                return StandardResponse.error("Ingredient name is required", "VALIDATION_ERROR", "ingredientName is mandatory");
            }

            if (ingredientRepository.existsByIngredientNameIgnoreCaseAndIsDeletedFalse(dto.getIngredientName().trim())) {
                return StandardResponse.error("Ingredient with name '" + dto.getIngredientName() + "' already exists",
                        "DUPLICATE_ERROR", "ingredientName must be unique");
            }

            KitchenIngredient entity = buildEntity(dto, null);
            entity.setIngredientCode(generateCode());
            ingredientRepository.save(entity);
            return StandardResponse.success("Ingredient created successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating ingredient: ", e);
            return StandardResponse.error("Failed to create ingredient", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  GET BY ID
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public StandardResponse<KitchenIngredientResponseDTO> getIngredientById(Long id) {
        try {
            KitchenIngredient entity = ingredientRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with ID: " + id));
            return StandardResponse.success(toResponseDTO(entity), "Ingredient fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching ingredient: ", e);
            return StandardResponse.error("Failed to fetch ingredient", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  GET ALL (with pagination + summary)
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public StandardResponse<KitchenIngredientPageResponse> getAllIngredients(Long categoryId, String search, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<KitchenIngredient> pageResult = ingredientRepository.findAllActive(categoryId, search, pageable);

            List<KitchenIngredientResponseDTO> dtos = pageResult.getContent()
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());

            // Summary: always over ALL active records (not just the current page filter)
            long total = ingredientRepository.countByIsDeletedFalse();
            long lowStock = ingredientRepository.countLowStock();
            long categories = ingredientRepository.countDistinctCategories();

            KitchenIngredientPageResponse response = KitchenIngredientPageResponse.builder()
                    .total(total)
                    .lowStock(lowStock)
                    .categories(categories)
                    .ingredients(dtos)
                    .totalRecords(pageResult.getTotalElements())
                    .currentPage(page)
                    .pageSize(size)
                    .totalPages(pageResult.getTotalPages())
                    .build();

            return StandardResponse.success(response, "Ingredients fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching ingredients: ", e);
            return StandardResponse.error("Failed to fetch ingredients", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<KitchenIngredientResponseDTO> updateIngredient(Long id, KitchenIngredientRequestDTO dto) {
        try {
            KitchenIngredient existing = ingredientRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with ID: " + id));

            if (dto.getIngredientName() != null && !dto.getIngredientName().isBlank()) {
                boolean nameConflict = ingredientRepository
                        .existsByIngredientNameIgnoreCaseAndIsDeletedFalseAndIdNot(dto.getIngredientName().trim(), id);
                if (nameConflict) {
                    return StandardResponse.error("Ingredient with name '" + dto.getIngredientName() + "' already exists",
                            "DUPLICATE_ERROR", "ingredientName must be unique");
                }
                existing.setIngredientName(dto.getIngredientName().trim());
            }

            applyUpdates(existing, dto);
            KitchenIngredient saved = ingredientRepository.save(existing);
            return StandardResponse.success(toResponseDTO(saved), "Ingredient updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating ingredient: ", e);
            return StandardResponse.error("Failed to update ingredient", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  DELETE (soft-delete)
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<Void> deleteIngredient(Long id) {
        try {
            KitchenIngredient existing = ingredientRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with ID: " + id));
            existing.setIsDeleted(true);
            ingredientRepository.save(existing);
            return StandardResponse.success("Ingredient deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting ingredient: ", e);
            return StandardResponse.error("Failed to delete ingredient", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ──────────────────────────────────────────────────────────────────────

    /** Build a new KitchenIngredient entity from DTO. Pass existingEntity=null for create. */
    private KitchenIngredient buildEntity(KitchenIngredientRequestDTO dto, KitchenIngredient existing) {
        KitchenIngredient entity = existing != null ? existing : KitchenIngredient.builder().build();

        if (dto.getIngredientName() != null)
            entity.setIngredientName(dto.getIngredientName().trim());

        // Category from CommonMaster
        if (dto.getCategoryId() != null) {
            CommonMaster cat = commonMasterRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));
            entity.setCategory(cat);
        }

        // Base Unit from CommonMaster
        if (dto.getBaseUnitId() != null) {
            CommonMaster baseUnit = commonMasterRepository.findById(dto.getBaseUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Base unit not found with ID: " + dto.getBaseUnitId()));
            entity.setBaseUnit(baseUnit);
        }

        // Purchase Unit from CommonMaster
        if (dto.getPurchaseUnitId() != null) {
            CommonMaster purchaseUnit = commonMasterRepository.findById(dto.getPurchaseUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Purchase unit not found with ID: " + dto.getPurchaseUnitId()));
            entity.setPurchaseUnit(purchaseUnit);
        }

        // Storage Type from CommonMaster
        if (dto.getStorageTypeId() != null) {
            CommonMaster storageType = commonMasterRepository.findById(dto.getStorageTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Storage type not found with ID: " + dto.getStorageTypeId()));
            entity.setStorageType(storageType);
        }

        if (dto.getPurchaseConversionFactor() != null)
            entity.setPurchaseConversionFactor(dto.getPurchaseConversionFactor());
        if (dto.getUsableYieldPercent() != null)
            entity.setUsableYieldPercent(dto.getUsableYieldPercent());
        if (dto.getCostPerPurchaseUnit() != null)
            entity.setCostPerPurchaseUnit(dto.getCostPerPurchaseUnit());
        if (dto.getCurrentStockLevel() != null)
            entity.setCurrentStockLevel(dto.getCurrentStockLevel());
        if (dto.getReorderThresholdLevel() != null)
            entity.setReorderThresholdLevel(dto.getReorderThresholdLevel());
        if (dto.getReorderQuantity() != null)
            entity.setReorderQuantity(dto.getReorderQuantity());
        if (dto.getPreferredSupplier() != null)
            entity.setPreferredSupplier(dto.getPreferredSupplier());

        return entity;
    }

    /** Apply all updatable fields from DTO to existing entity. */
    private void applyUpdates(KitchenIngredient existing, KitchenIngredientRequestDTO dto) {
        buildEntity(dto, existing);
    }

    /** Convert entity → Response DTO (resolves CommonMaster names). */
    private KitchenIngredientResponseDTO toResponseDTO(KitchenIngredient e) {
        return KitchenIngredientResponseDTO.builder()
                .id(e.getId())
                .ingredientCode(e.getIngredientCode())
                .ingredientName(e.getIngredientName())
                .categoryId(e.getCategory() != null ? e.getCategory().getId() : null)
                .categoryName(e.getCategory() != null ? e.getCategory().getValue() : null)
                .baseUnitId(e.getBaseUnit() != null ? e.getBaseUnit().getId() : null)
                .baseUnitName(e.getBaseUnit() != null ? e.getBaseUnit().getValue() : null)
                .purchaseUnitId(e.getPurchaseUnit() != null ? e.getPurchaseUnit().getId() : null)
                .purchaseUnitName(e.getPurchaseUnit() != null ? e.getPurchaseUnit().getValue() : null)
                .purchaseConversionFactor(e.getPurchaseConversionFactor())
                .usableYieldPercent(e.getUsableYieldPercent())
                .costPerPurchaseUnit(e.getCostPerPurchaseUnit())
                .currentStockLevel(e.getCurrentStockLevel())
                .reorderThresholdLevel(e.getReorderThresholdLevel())
                .reorderQuantity(e.getReorderQuantity())
                .storageTypeId(e.getStorageType() != null ? e.getStorageType().getId() : null)
                .storageTypeName(e.getStorageType() != null ? e.getStorageType().getValue() : null)
                .preferredSupplier(e.getPreferredSupplier())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /** Generate sequential code: ING-001, ING-002 … */
    private String generateCode() {
        long count = ingredientRepository.countAll() + 1;
        return String.format("ING-%03d", count);
    }
}
