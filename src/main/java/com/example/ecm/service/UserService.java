package com.example.ecm.service;

import com.example.ecm.dto.CreateUserRequest;
import com.example.ecm.dto.CreateUserResponse;
import com.example.ecm.mapper.UserMapper;
import com.example.ecm.model.User;
import com.example.ecm.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для выполнения операций с пользователями, включая создание, получение,
 * обновление и удаление пользователей.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Конструктор для внедрения зависимости UserRepository и UserMapper.
     *
     * @param userRepository Репозиторий для работы с пользователями
     * @param userMapper     Маппер для преобразования между User и DTO
     */
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Создание нового пользователя на основе данных из DTO.
     *
     * @param createUserRequest DTO с данными для создания нового пользователя
     * @return DTO с данными сохраненного пользователя
     */
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.toUser(createUserRequest);
        User savedUser = userRepository.save(user);
        return userMapper.toCreateUserResponse(savedUser);
    }

    /**
     * Получение пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     * @return Optional с объектом пользователя, если найден
     */
    public Optional<CreateUserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toCreateUserResponse);
    }

    /**
     * Получение списка всех пользователей с преобразованием в DTO.
     *
     * @return Список DTO с данными пользователей
     */
    public List<CreateUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toCreateUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Обновление существующего пользователя по ID на основе данных из DTO.
     *
     * @param id                Идентификатор пользователя
     * @param updateUserRequest DTO с обновленными данными пользователя
     * @return Обновленный объект пользователя в виде DTO
     */
    public CreateUserResponse updateUser(Long id, CreateUserRequest updateUserRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(updateUserRequest.getName());
                    existingUser.setSurname(updateUserRequest.getSurname());
                    existingUser.setEmail(updateUserRequest.getEmail());
                    existingUser.setPassword(updateUserRequest.getPassword());
                    User updatedUser = userRepository.save(existingUser);
                    return userMapper.toCreateUserResponse(updatedUser);
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

