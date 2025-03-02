package com.ugustavob.finsuppapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponseDTO<T> {
    private String message;
    private String type;
    private T data;
    private List<T> dataList;
    private PaginationInfo pagination;

    public SuccessResponseDTO(String message) {
        this.message = message;
        this.type = "Success";
        this.data = null;
        this.dataList = null;
        this.pagination = null;
    }


    public SuccessResponseDTO(String message, T data) {
        this.message = message;
        this.type = "Success";
        this.data = data;
        this.dataList = null;
        this.pagination = null;
    }

    public SuccessResponseDTO(String message, List<T> dataList) {
        this.message = message;
        this.type = "Success";
        this.data = null;
        this.dataList = dataList;
        this.pagination = null;
    }

    public SuccessResponseDTO(String message, Page<T> page) {
        this.message = message;
        this.type = "Success";
        this.data = null;
        this.dataList = page.getContent();
        this.pagination = new PaginationInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int currentPage;
        private int pageSize;
        private int totalPages;
        private long totalElements;
    }
}