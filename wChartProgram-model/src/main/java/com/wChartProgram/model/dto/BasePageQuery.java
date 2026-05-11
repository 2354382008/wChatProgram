package com.wChartProgram.model.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * 通用分页查询基类
 * 所有查询DTO/实体直接继承即可
 */
@Data
public class BasePageQuery {

    /**
     * 当前页码（默认第1页）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数（默认10条）
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式 asc/desc
     */
    private String sortOrder;

    /**
     * 转换成 Spring Pageable
     * 适配 Spring Data 分页，pageNum 从1转0
     */
    public Pageable getPageable() {
        // 页码容错
        pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;

        // 转换为Spring要求的从0开始页码
        int page = pageNum - 1;

        // 无排序
        if (sortField == null || StringUtils.hasText(sortField)) {
            return PageRequest.of(page, pageSize);
        }

        // 有排序
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        return PageRequest.of(page, pageSize, Sort.by(direction, sortField));
    }
}