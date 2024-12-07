package com.example.ecm.repository;

import com.example.ecm.model.Attribute;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями Attribute.
 * Предоставляет стандартные методы для взаимодействия с базой данных,
 * такие как создание, чтение, обновление и удаление (CRUD) атрибутов документов.
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Optional<Attribute> findByName(String name);
    List<Attribute> findAttributesByIdIsIn(List<Long> ids);
    @NotNull Page<Attribute> findAll(@NotNull Pageable pageable);
    Boolean existsByName(String name);
}
