package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    @JsonProperty("parent_category_id")
    private Long parentCategoryId;

    private String description;
    @JsonProperty("parent_category_name")
    private String parentCategoryName;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static CategoryResponse fromEntity(Category category) {
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
        if(category.getParentCategory() != null) {
            categoryResponse.setParentCategoryId(category.getParentCategory().getId());
            categoryResponse.setParentCategoryName(category.getParentCategory().getName());
        }
        else{
            categoryResponse.setParentCategoryId(0L);
            categoryResponse.setParentCategoryName("null");
        }
        return categoryResponse;
    }
}
