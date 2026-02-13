package com.example.FurnitureShop.DTO.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    private List<T> content;

    @JsonProperty("total_elements")
    private int totalElements;

    @JsonProperty("page_number")
    private int pageNumber;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("has_next")
    private boolean hasNext;

    @JsonProperty("has_previous")
    private boolean hasPrevious;

    @JsonProperty("is_first")
    private boolean isFirst;

    @JsonProperty("is_last")
    private boolean isLast;

    public static <T> PageResponse<T> fromPage(Page<T> page){
        if(page == null){
            return PageResponse.<T>builder()
                    .pageSize(0)
                    .totalElements(0)
                    .pageNumber(0)
                    .totalPages(0)
                    .hasNext(false)
                    .hasPrevious(false)
                    .isFirst(true)
                    .isLast(true)
                    .content(Collections.emptyList())
                    .build();
        }
        return PageResponse.<T>builder()
                .content(page.getContent())
                .hasNext(page.hasNext())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .hasPrevious(page.hasPrevious())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .totalElements(page.getNumberOfElements())
                .build();
    }
}
