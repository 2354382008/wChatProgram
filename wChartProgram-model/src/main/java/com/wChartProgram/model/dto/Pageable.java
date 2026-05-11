package com.wChartProgram.model.dto;

import org.springframework.beans.BeanUtils;

import java.util.Collection;

public final class Pageable<T> {
    private final Collection<T> data;
    private final Long total;

    private Pageable(Collection<T> data, Long total) {
        this.data = data;
        this.total = total;
    }

    public static <T> Pageable<T> of(Collection<T> data, long total) {
        return new Pageable(data, total);
    }

    public Collection<T> getData() {
        return this.data;
    }

    public Long getTotal() {
        return this.total;
    }
}
