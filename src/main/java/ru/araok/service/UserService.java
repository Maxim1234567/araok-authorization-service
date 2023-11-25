package ru.araok.service;

import ru.araok.dto.UserDto;

public interface UserService {

    UserDto save(UserDto user);

    UserDto getByName(String name);

    UserDto getById(Long id);

    UserDto getByPhoneAndPassword(String phone, String password);
}
