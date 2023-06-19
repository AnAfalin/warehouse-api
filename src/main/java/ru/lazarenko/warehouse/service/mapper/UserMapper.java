package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.dto.UserDto;
import ru.lazarenko.warehouse.dto.registration.UserRegisterRequest;
import ru.lazarenko.warehouse.entity.Category;
import ru.lazarenko.warehouse.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRegisterRequest request);

    List<UserDto> toUserDtoList(List<User> users);
}
