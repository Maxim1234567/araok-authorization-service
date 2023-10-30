package ru.araok.service;

import ru.araok.dto.UserDto;

public interface UserService {
    UserDto getByName(String name);

    UserDto getById(Long id);

    UserDto getByNameAndPassword(String name, String password);
}
