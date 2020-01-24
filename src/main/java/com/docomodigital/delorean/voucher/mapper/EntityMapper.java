package com.docomodigital.delorean.voucher.mapper;

import java.util.List;

/**
 * Contract for a generic dto to entity mapper.
 *
 * @param <D> - DTO type parameter.
 * @param <E> - Entity type parameter.
 */

public interface EntityMapper<D, E> {

    /**
     * Maps a dto to an entity
     *
     * @param dto dto to map
     * @return the mapped entity
     */
    E toEntity(D dto);

    /**
     * Maps an entity to a dto
     *
     * @param entity entity to map
     * @return the mapped dto
     */
    D toDto(E entity);

    /**
     * Maps a dto list to an entity list
     *
     * @param dtoList dto list to map
     * @return the mapped entity list
     */
    List<E> toEntity(List<D> dtoList);

    /**
     * Maps a entity list to a dto list
     *
     * @param entityList entity list to map
     * @return the mapped dto list
     */
    List<D> toDto(List<E> entityList);
}
