package com.app.user_service.common.pagination;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationMapper {

  public <T> PaginationResponse<T> toPaginationResponse(Page<T> page) {
    return new PaginationResponse<>(
        page.getContent(),
        new PaginationResponse.PaginationMetadata(
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalPages(),
            page.getTotalElements()));
  }

}

