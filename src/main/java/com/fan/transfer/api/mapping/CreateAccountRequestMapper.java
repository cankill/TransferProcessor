package com.fan.transfer.api.mapping;

import com.fan.transfer.api.model.CreateAccountRequest;
import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(nullValueMappingStrategy = RETURN_DEFAULT)
public interface CreateAccountRequestMapper {
    CreateAccountRequestMapper INSTANCE = Mappers.getMapper(CreateAccountRequestMapper.class);

    @Mapping(target = "id", source = "generatedId")
    @Mapping(target = "currency", source = "createAccountRequest.currency")
    @Mapping(target = "balance", source = "createAccountRequest.balance")
    @Mapping(target = "userId", source = "userId")
    Account mapToAccount (User.Id userId, Account.Id generatedId, CreateAccountRequest createAccountRequest);
}
