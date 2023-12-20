package az.ubank.msauth.service.impl;


import az.ubank.msauth.dao.entity.UserEntity;
import az.ubank.msauth.dao.repo.UserRepo;
import az.ubank.msauth.dto.request.UserCreateDto;
import az.ubank.msauth.dto.response.UserResponseDto;
import az.ubank.msauth.exception.*;
import az.ubank.msauth.mapper.UserMapper;
import az.ubank.msauth.service.UserService;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static az.ubank.msauth.exception.ErrorCodes.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String customerPin) throws UsernameNotFoundException {
        log.info("service loadUserByUsername start Customer PIN: {}", customerPin);
        UserEntity userEntity = getUserOrElseThrow(customerPin);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails
                .User(userEntity.getCustomerPin(), userEntity.getPassword(), authorities);
    }

    @Override
    public UserResponseDto saveUser(UserCreateDto userCreateDto) {
        log.info("service saveUser started new user :{}", userCreateDto.getName());
        checkIsNullOrEmpty(userCreateDto);
        checkUserExisted(userCreateDto);
        customerPinCheck(userCreateDto.getCustomerPin());
        emailCheck(userCreateDto.getEmail());
        passwordCheck(userCreateDto.getPassword());
        userCreateDto.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        UserEntity entity = userRepo.save(userMapper.toUserEntity(userCreateDto));
        log.info("service saveUser completed new user :{}", userCreateDto.getName());
        return userMapper.toUserResponseDto(entity);
    }

    @Override
    public UserResponseDto getUser(String customerPin) {
        log.info("service getUser Fetching user {}", customerPin);

        return userMapper.toUserResponseDto(userRepo.findByCustomerPin(customerPin)
                .orElseThrow(
                        () -> new UserNotFoundException(customerPin + "customerPin not found")));
    }

    @Override
    public List<UserResponseDto> getUsers() {
        log.info("service getUsers Fetching all users");
        return userRepo.findAll().stream().map(userMapper::toUserResponseDto).toList();

    }

    private void checkUserExisted(UserCreateDto userCreateDto) {
        if (userRepo.findByCustomerPin(userCreateDto.getCustomerPin()).isPresent()) {
            throw new UserExistedException(userCreateDto.getCustomerPin() + " user was existed");
        }
    }

    private void customerPinCheck(String customerPin) {
        String pinPattern = "^[a-zA-Z0-9]{7}$";
        if (!patternMatches(customerPin, pinPattern)) {
            throw new EmailNotCorrectException(INCORRECT_PIN);
        }
    }

    private void emailCheck(String email) {
        String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (!patternMatches(email, emailPattern)) {
            throw new EmailNotCorrectException(EMAIL_WRONG);
        }
    }

    /*          # start-of-string;
                # a digit must occur at least once;
                # a lower case letter must occur at least once;
                # an upper case letter must occur at least once;
                # a special character must occur at least once;
                # no whitespace allowed in the entire string;
                # anything, at least eight places though;
                # end-of-string*/
    private void passwordCheck(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        //change this message
        String errorMsg = """
                Please Enter Valid Password.
                Ex: Password123!, Pass2021!@, Test1234# etc.
                """;
        if (patternMatches(password, passwordPattern)) {
            throw new WrongPasswordException(PASSWORD_WRONG_REGEX + " | " + errorMsg);
        }
    }

    private UserEntity getUserOrElseThrow(String customerPin) {
        return userRepo.findByCustomerPin(customerPin).orElseThrow(
                () -> new UserNotFoundException(customerPin + "customerPin not found in the DB"));
    }

    private void checkIsNullOrEmpty(UserCreateDto userCreateDto) {
        if (Strings.isNullOrEmpty(userCreateDto.getCustomerPin())
                || Strings.isNullOrEmpty(userCreateDto.getName())
                || Strings.isNullOrEmpty(userCreateDto.getSurname())
                || Strings.isNullOrEmpty(userCreateDto.getEmail())
                || Strings.isNullOrEmpty(userCreateDto.getPassword())) {
            throw new NullBlankFoundException(NULL_FIELD);
        }
    }

    private static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern).matcher(emailAddress).matches();
    }
}
