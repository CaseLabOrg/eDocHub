package com.example.ecm.service;

import com.example.ecm.model.User;
import com.example.ecm.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для выполнения операций с пользователями, включая создание, получение,
 * обновление и удаление пользователей.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Конструктор для внедрения зависимости UserRepository.
     *
     * @param userRepository Репозиторий для работы с пользователями
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создание нового пользователя.
     *
     * @param user Объект User для сохранения
     * @return Сохраненный объект пользователя
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Получение пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     * @return Optional с объектом пользователя, если найден
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Получение списка всех пользователей.
     *
     * @return Список пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Обновление существующего пользователя по ID.
     *
     * @param id          Идентификатор пользователя
     * @param updatedUser Объект с обновленными данными пользователя
     * @return Обновленный объект пользователя
     */
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(updatedUser.getEmail());
                    user.setFirstName(updatedUser.getFirstName());
                    user.setMiddleName(updatedUser.getMiddleName());
                    user.setLastName(updatedUser.getLastName());
                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Удаление пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
