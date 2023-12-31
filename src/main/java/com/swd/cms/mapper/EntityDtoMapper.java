package com.swd.cms.mapper;

import java.util.List;

public interface EntityDtoMapper<D,E> {
    E toEntity(D dto);
    D toDto(E entity);

    List<E> toEntityList(List<D> dtoList);
    List<D> toDtoList(List<E> entityList);
}
