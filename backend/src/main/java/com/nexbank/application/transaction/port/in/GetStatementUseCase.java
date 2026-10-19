package com.nexbank.application.transaction.port.in;

import com.nexbank.application.shared.PagedResult;
import com.nexbank.domain.transaction.FinancialTransaction;

public interface GetStatementUseCase {
    PagedResult<FinancialTransaction> getStatement(GetStatementQuery query);
}
