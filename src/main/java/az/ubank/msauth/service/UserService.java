package az.ubank.msauth.service;


import az.ubank.msauth.dto.request.UserCreateDto;
import az.ubank.msauth.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto saveUser(UserCreateDto userCreateDto);

    UserResponseDto getUser(String username);

    List<UserResponseDto> getUsers();
}
