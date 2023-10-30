package ru.araok.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.araok.dto.UserDto;
import ru.araok.exception.AuthException;
import ru.araok.repository.UserRepository;
import ru.araok.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getByName(String name) {
        return userRepository.findByNameIgnoreCase(name)
                .map(UserDto::toDto)
                .orElseThrow(AuthException::new);
    }

    @Override
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::toDto)
                .orElseThrow(AuthException::new);
    }

    @Override
    public UserDto getByNameAndPassword(String name, String password) {
        return userRepository.findByNameAndPassword(name, password)
                .map(UserDto::toDto)
                .orElseThrow(AuthException::new);
    }
}
