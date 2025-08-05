package com.sivalabs.blog.shared.models;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PagedResult<T>(
        List<T> data,
        int currentPageNo,
        int totalPages,
        long totalElements,
        boolean hasNextPage,
        boolean hasPreviousPage) {

    public static <T> PagedResult<T> from(Page<T> page) {
        return new PagedResult<>(
                page.getContent(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious());
    }

    public <R> PagedResult<R> map(Function<T, R> converter) {
        return new PagedResult<>(
                this.data.stream().map(converter).toList(),
                this.currentPageNo,
                this.totalPages,
                this.totalElements,
                this.hasNextPage,
                this.hasPreviousPage);
    }

    public static <T, R> PagedResult<R> getPagedResult(
            int pageNo, int pageSize, Function<Pageable, Page<T>> pageSupplier, Function<T, R> mapper) {
        Sort sort = Sort.by("createdAt").descending();
        int page = pageNo > 0 ? pageNo - 1 : 0;
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<R> result = pageSupplier.apply(pageable).map(mapper);
        return PagedResult.from(result);
    }
}
