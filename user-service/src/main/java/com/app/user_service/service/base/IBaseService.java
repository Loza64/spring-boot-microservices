package com.app.user_service.service.base;

public interface IBaseService<ID, CreateDto, UpdateDto, ResponseDto> {
  ResponseDto create(CreateDto dto);

  ResponseDto update(ID id, UpdateDto dto);

  ResponseDto findById(ID id);

  void delete(ID id);

  void restore(ID id);
}