package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateUserRequest;
import com.example.ecm.dto.responses.CreateUserResponse;
import com.example.ecm.dto.requests.PutRoleRequest;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.UserMapper;
import com.example.ecm.model.Role;
import com.example.ecm.model.User;
import com.example.ecm.repository.RoleRepository;
import com.example.ecm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для выполнения операций с пользователями, включая создание, получение,
 * обновление и удаление пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    /**
     * Создание нового пользователя на основе данных из DTO.
     *
     * @param createUserRequest DTO с данными для создания нового пользователя
     * @return DTO с данными сохраненного пользователя
     */
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.toUser(createUserRequest);
        user.setRoles(Set.of(roleRepository.findByName("USER").orElseThrow(() -> new NotFoundException("Role with name: USER not found"))));
        user.setPassword(encoder.encode(createUserRequest.getPassword()));
        User savedUser = userRepository.save(user);

        return userMapper.toCreateUserResponse(savedUser);
    }

    /**
     * Получение пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     * @return Optional с объектом пользователя, если найден
     */
    public CreateUserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toCreateUserResponse).orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
    }

    public CreateUserResponse addRole(Long id, PutRoleRequest request) {
        Role role = roleRepository.findByName(request.getName().toUpperCase()).orElseThrow(() -> new NotFoundException("Role with name: " + request.getName().toUpperCase() + " not found"));
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        user.getRoles().add(role);
        return userMapper.toCreateUserResponse(userRepository.save(user));
    }

    public CreateUserResponse removeRole(Long id, PutRoleRequest request) {
        Role role = roleRepository.findByName(request.getName().toUpperCase()).orElseThrow(() -> new NotFoundException("Role with name: " + request.getName().toUpperCase() + " not found"));
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        user.getRoles().remove(role);
        return userMapper.toCreateUserResponse(userRepository.save(user));
    }

    /**
     * Получение списка всех пользователей с преобразованием в DTO.
     *
     * @return Список DTO с данными пользователей
     */
    public List<CreateUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toCreateUserResponse)
                .toList();
    }

    /**
     * Обновление существующего пользователя по ID на основе данных из DTO.
     *
     * @param id                Идентификатор пользователя
     * @param updateUserRequest DTO с обновленными данными пользователя
     * @return Обновленный объект пользователя в виде DTO
     */
    public CreateUserResponse updateUser(Long id, CreateUserRequest updateUserRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        user.setName(updateUserRequest.getName());
        user.setSurname(updateUserRequest.getSurname());
        user.setEmail(updateUserRequest.getEmail());
        user.setPassword(encoder.encode(updateUserRequest.getPassword()));
        User updatedUser = userRepository.save(user);

        return userMapper.toCreateUserResponse(updatedUser);
    }

    /**
     * Удаление пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        userRepository.delete(user);
    }

    /**
     * Поиск пользователя по email
     *
     * @param email Почта пользователя
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}

