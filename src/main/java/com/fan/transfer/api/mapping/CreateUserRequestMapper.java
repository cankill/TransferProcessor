package com.fan.transfer.api.mapping;

import com.fan.transfer.api.model.CreateUserRequest;
import com.fan.transfer.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(nullValueMappingStrategy = RETURN_DEFAULT)
public interface CreateUserRequestMapper {
    CreateUserRequestMapper INSTANCE = Mappers.getMapper(CreateUserRequestMapper.class);

    @Mapping(target = "id", source = "generatedId")
    @Mapping(target = "name", source = "createUserRequest.name")
    @Mapping(target = "email", source = "createUserRequest.email")
    @Mapping(target = "phone", source = "createUserRequest.phone")
    User mapToUser (User.Id generatedId, CreateUserRequest createUserRequest);
}
