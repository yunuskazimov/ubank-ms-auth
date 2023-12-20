package az.ubank.msauth.mapper;

import az.ubank.msauth.dao.entity.UserEntity;
import az.ubank.msauth.dto.request.UserCreateDto;
import az.ubank.msauth.dto.response.UserResponseDto;
import org.mapstruct.factory.Mappers;


public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDto toUserResponseDto(UserEntity user);

    UserEntity toUserEntity(UserCreateDto userCreateDto);

}
