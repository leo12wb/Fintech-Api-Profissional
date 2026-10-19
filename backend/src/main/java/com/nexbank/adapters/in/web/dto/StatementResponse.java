package com.nexbank.adapters.in.web.dto;

import java.util.List;

public record StatementResponse(
    List<TransactionResponse> transactions,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {}
