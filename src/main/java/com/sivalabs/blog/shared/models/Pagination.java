package com.sivalabs.blog.shared.models;

import java.util.HashMap;
import java.util.Map;

public class Pagination<T> {
    private final PagedResult<T> pagedResult;
    private final String paginationUrlPrefix;
    private final Map<String, Object> queryParams;

    public Pagination(PagedResult<T> pagedResult, String paginationUrlPrefix) {
        this(pagedResult, paginationUrlPrefix, Map.of());
    }

    public Pagination(PagedResult<T> pagedResult, String paginationUrlPrefix, Map<String, Object> queryParams) {
        this.pagedResult = pagedResult;
        this.paginationUrlPrefix = paginationUrlPrefix;
        this.queryParams = queryParams;
    }

    public boolean hasData() {
        return !pagedResult.data().isEmpty();
    }

    public int currentPageNo() {
        return pagedResult.currentPageNo();
    }

    public int totalPages() {
        return pagedResult.totalPages();
    }

    public long totalElements() {
        return pagedResult.totalElements();
    }

    public boolean isFirstPage() {
        return pagedResult.currentPageNo() == 1;
    }

    public boolean isLastPage() {
        return pagedResult.currentPageNo() == pagedResult.totalPages();
    }

    public boolean hasNextPage() {
        return pagedResult.hasNextPage();
    }

    public boolean hasPreviousPage() {
        return pagedResult.hasPreviousPage();
    }

    public String getFirstPageLink() {
        if (isFirstPage()) {
            return "#";
        }
        Map<String, Object> queryParams = new HashMap<>(Map.copyOf(this.queryParams));
        queryParams.put("page", 1);
        return paginationUrlPrefix + "?" + getQueryString(queryParams);
    }

    public String getLastPageLink() {
        if (isLastPage()) {
            return "#";
        }
        Map<String, Object> queryParams = new HashMap<>(Map.copyOf(this.queryParams));
        queryParams.put("page", pagedResult.totalPages());
        return paginationUrlPrefix + "?" + getQueryString(queryParams);
    }

    public String getPreviousPageLink() {
        if (!hasPreviousPage()) {
            return "#";
        }
        Map<String, Object> queryParams = new HashMap<>(Map.copyOf(this.queryParams));
        queryParams.put("page", pagedResult.currentPageNo() - 1);
        return paginationUrlPrefix + "?" + getQueryString(queryParams);
    }

    public String getNextPageLink() {
        if (!hasNextPage()) {
            return "#";
        }
        Map<String, Object> queryParams = new HashMap<>(Map.copyOf(this.queryParams));
        queryParams.put("page", pagedResult.currentPageNo() + 1);
        return paginationUrlPrefix + "?" + getQueryString(queryParams);
    }

    private String getQueryString(Map<String, Object> queryParams) {
        if (queryParams.isEmpty()) {
            return "";
        }
        return queryParams.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((s1, s2) -> s1 + "&" + s2)
                .orElse("");
    }
}
