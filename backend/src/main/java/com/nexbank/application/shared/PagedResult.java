package com.nexbank.application.shared;

import java.util.List;

public record PagedResult<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {
    public boolean hasNext() { return pageNumber < totalPages - 1; }
}
