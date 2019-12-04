package com.fan.transfer.api.mapping;

import com.fan.transfer.api.model.CreateUserResponse;
import com.fan.transfer.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateUserResponseMapper {
    CreateUserResponseMapper INSTANCE = Mappers.getMapper(CreateUserResponseMapper.class);

    default String mapId(User.Id value) {
        return value.getValue();
    }

    CreateUserResponse mapToCreateUserResponse (User user);
}
