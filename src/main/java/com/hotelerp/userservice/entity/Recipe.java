package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe / Bill of Materials (BOM) for a MenuItem.
 * One MenuItem can have exactly one active recipe.
 */
@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The dish this recipe belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    /** Friendly recipe name, e.g. "Paneer Tikka Recipe". */
    @Column(name = "recipe_name", nullable = false, length = 200)
    private String recipeName;

    /** Portion size (quantity produced per batch, e.g. 1 PLATE). */
    @Column(name = "portion_size", precision = 10, scale = 4)
    private BigDecimal portionSize;

    /** Portion unit label, e.g. "PLATE", "BOWL". */
    @Column(name = "portion_unit", length = 50)
    private String portionUnit;

    /** Prep time in minutes. */
    @Column(name = "prep_time_mins")
    private Integer prepTimeMins;

    /** Cooking instructions / preparation notes. */
    @Lob
    @Column(name = "cooking_instructions", columnDefinition = "TEXT")
    private String cookingInstructions;

    /**
     * Calculated total ingredient cost per portion.
     * Stored for quick reads; recomputed on every save.
     */
    @Column(name = "portion_cost", precision = 14, scale = 4)
    private BigDecimal portionCost;

    /** BOM lines – cascade all so lines are persisted / deleted together. */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDeleted == null) isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
