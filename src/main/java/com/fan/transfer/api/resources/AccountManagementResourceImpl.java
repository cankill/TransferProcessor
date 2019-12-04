package com.fan.transfer.api.resources;

import com.fan.transfer.api.mapping.CreateAccountRequestMapper;
import com.fan.transfer.api.mapping.CreateAccountResponseMapper;
import com.fan.transfer.api.mapping.GetBalanceResponseMapper;
import com.fan.transfer.api.model.CreateAccountRequest;
import com.fan.transfer.api.model.TransferRequest;
import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.User;
import com.fan.transfer.services.AccountCommandManager;
import com.fan.transfer.services.AccountQueryManager;
import com.fan.transfer.services.ValidationService;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import javax.ws.rs.core.Response;
import java.util.UUID;

public class AccountManagementResourceImpl implements AccountManagementResource {
    private static final GetBalanceResponseMapper BALANCE_RESPONSE_MAPPER = GetBalanceResponseMapper.INSTANCE;
    private static final CreateAccountRequestMapper CREATE_ACCOUNT_REQUEST_MAPPER = CreateAccountRequestMapper.INSTANCE;
    private static final CreateAccountResponseMapper CREATE_ACCOUNT_RESPONSE_MAPPER = CreateAccountResponseMapper.INSTANCE;

    private AccountCommandManager accountCommandManager;

    private AccountQueryManager accountQueryManager;

    private ValidationService validationService;

    private User user;

    @AssistedInject
    AccountManagementResourceImpl(AccountCommandManager accountCommandManager,
                                  AccountQueryManager accountQueryManager,
                                  ValidationService validationService,
                                  @Assisted User user) {
        this.accountCommandManager = accountCommandManager;
        this.accountQueryManager = accountQueryManager;
        this.validationService = validationService;
        this.user = user;
    }

    @Override
    public Response create (CreateAccountRequest createAccountRequest) {
        Account.Id generatedId = generateId();
        Account account = CREATE_ACCOUNT_REQUEST_MAPPER.mapToAccount(user.getId(), generatedId, createAccountRequest);
        accountCommandManager.create(account);
        
        return Response.status(Response.Status.CREATED)
                .entity(CREATE_ACCOUNT_RESPONSE_MAPPER.mapToCreateAccountResponse(account))
                .build();
    }

    @Override
    public Response getBalance (Account.Id accountId) {
        Account account = accountQueryManager.get(accountId);
        return Response.status(Response.Status.OK)
                       .entity(BALANCE_RESPONSE_MAPPER.mapToBalance(account))
                       .build();
    }

    @Override
    public Response doTransfer (TransferRequest request) {
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
    private Account.Id generateId () {
        return Account.Id.valueOf(UUID.randomUUID().toString());
    }
}
