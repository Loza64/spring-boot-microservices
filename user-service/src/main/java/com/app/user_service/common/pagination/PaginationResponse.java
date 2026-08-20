package com.app.user_service.common.pagination;

import java.util.List;

public record PaginationResponse<T>(List<T> data, PaginationMetadata pagination) {
  public record PaginationMetadata(
      int page,
      int pageSize,
      int pageCount,
      long total) {
  }
}