package com.example.ecm.repository;

import com.example.ecm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями User.
 * Обеспечивает стандартные операции CRUD для пользователей.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Здесь могут быть добавлены дополнительные методы для специфичных запросов к пользователям, если потребуется
}
