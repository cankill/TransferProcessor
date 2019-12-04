package com.fan.transfer.api.mapping;

import com.fan.transfer.api.model.CreateAccountResponse;
import com.fan.transfer.domain.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateAccountResponseMapper {
    CreateAccountResponseMapper INSTANCE = Mappers.getMapper(CreateAccountResponseMapper.class);

    default String mapId(Account.Id value) {
        return value.getValue();
    }

    CreateAccountResponse mapToCreateAccountResponse (Account account);
}
