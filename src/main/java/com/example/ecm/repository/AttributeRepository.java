package com.example.ecm.repository;

import com.example.ecm.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями Attribute.
 * Предоставляет стандартные методы для взаимодействия с базой данных,
 * такие как создание, чтение, обновление и удаление (CRUD) атрибутов документов.
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
}