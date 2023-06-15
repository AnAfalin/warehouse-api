package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.warehouse.dto.registration.UserRegisterRequest;
import ru.lazarenko.warehouse.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRegisterRequest request);
}
