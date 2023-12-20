package az.ubank.msauth.controller;

import az.ubank.msauth.dto.request.UserCreateDto;
import az.ubank.msauth.dto.response.UserResponseDto;
import az.ubank.msauth.service.TokenService;
import az.ubank.msauth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto saveUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userService.saveUser(userCreateDto);
    }

    @GetMapping("/users")//For Admins
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/user/{customerPin}")
    public UserResponseDto getUser(@PathVariable String customerPin) {
        return userService.getUser(customerPin);
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        tokenService.refreshToken(request, response);
    }

    @PostMapping("/token/active")
    public void activeToken(HttpServletRequest request, HttpServletResponse response) {
        tokenService.activeToken(request, response);
    }
}


