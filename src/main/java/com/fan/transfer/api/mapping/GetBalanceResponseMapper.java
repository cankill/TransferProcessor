package com.fan.transfer.api.mapping;

import com.fan.transfer.api.model.GetBalanceResponse;
import com.fan.transfer.domain.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.text.NumberFormat;

/**
 * Mapstruct mapper interface to map from API Request model to Domain model
 */
@Mapper
public interface GetBalanceResponseMapper {
    GetBalanceResponseMapper INSTANCE = Mappers.getMapper(GetBalanceResponseMapper.class);
    
    @Mapping(target = "accountId", expression = "java(account.getId().getValue())")
    @Mapping(target = "balance", expression = "java(getBalance(account))")
    @Mapping(target = "currency", expression = "java(getCurrency(account))")
    GetBalanceResponse mapToBalance(Account account);

    default String getBalance(Account account) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setCurrency(account.getCurrency());
        return nf.format(account.getBalance());
    }

    default String getCurrency(Account account) {
        return account.getCurrency().getCurrencyCode();
    }
}
