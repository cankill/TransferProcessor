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
import com.fan.transfer.services.tm.TransferCommandManager;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.UUID;

public class AccountManagementResourceImpl implements AccountManagementResource {
    private static final GetBalanceResponseMapper BALANCE_RESPONSE_MAPPER = GetBalanceResponseMapper.INSTANCE;
    private static final CreateAccountRequestMapper CREATE_ACCOUNT_REQUEST_MAPPER = CreateAccountRequestMapper.INSTANCE;
    private static final CreateAccountResponseMapper CREATE_ACCOUNT_RESPONSE_MAPPER = CreateAccountResponseMapper.INSTANCE;

    private AccountCommandManager accountCommandManager;

    private AccountQueryManager accountQueryManager;

    private TransferCommandManager transferCommandManager;

    private User user;

    @AssistedInject
    AccountManagementResourceImpl(AccountCommandManager accountCommandManager,
                                  AccountQueryManager accountQueryManager,
                                  TransferCommandManager transferCommandManager,
                                  @Assisted User user) {
        this.accountCommandManager = accountCommandManager;
        this.accountQueryManager = accountQueryManager;
        this.transferCommandManager = transferCommandManager;
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
        validateTransferRequest(request);

        transferCommandManager.transfer(request.getFrom(), request.getTo(), request.getAmount());
        return Response.status(Response.Status.OK).build();
    }

    private void validateTransferRequest (TransferRequest request) {
        if(request.getFrom() == null) {
            throw new IllegalArgumentException(String.format("TransferRequest %s should contains non empty field 'from'", request));
        }

        if(request.getTo() == null) {
            throw new IllegalArgumentException(String.format("TransferRequest %s should contains non empty field 'to'", request));
        }

        if(request.getAmount() == null) {
            throw new IllegalArgumentException(String.format("TransferRequest %s should contains non empty field 'amount'", request));
        }

        if(request.getAmount().compareTo(BigDecimal.ZERO) < 1) {
            throw new IllegalArgumentException(String.format("TransferRequest %s should contains non negative field 'amount'", request));
        }
    }

    private Account.Id generateId () {
        return Account.Id.valueOf(UUID.randomUUID().toString());
    }
}
