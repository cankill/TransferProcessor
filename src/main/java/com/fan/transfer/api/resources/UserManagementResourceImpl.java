package com.fan.transfer.api.resources;

import com.fan.transfer.api.mapping.CreateUserRequestMapper;
import com.fan.transfer.api.mapping.CreateUserResponseMapper;
import com.fan.transfer.api.model.CreateUserRequest;
import com.fan.transfer.di.AccountManagementResourceFactory;
import com.fan.transfer.domain.User;
import com.fan.transfer.services.UserCommandManager;
import com.fan.transfer.services.UserQueryManager;
import com.fan.transfer.services.ValidationService;
import com.google.inject.Inject;

import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class UserManagementResourceImpl implements UserManagementResource {
    private static final CreateUserRequestMapper CREATE_USER_REQUEST_MAPPER = CreateUserRequestMapper.INSTANCE;
    private static final CreateUserResponseMapper CREATE_USER_RESPONSE_MAPPER = CreateUserResponseMapper.INSTANCE;

    @Inject
    private UserQueryManager userQueryManager;

    @Inject
    private UserCommandManager userCommandManager;

    @Inject
    private AccountManagementResourceFactory accountManagementResourceFactory;

    @Inject
    private ValidationService validationService;

    @Context
    private ResourceContext resourceContext;

    @Override
    public Response create (CreateUserRequest createUserRequest) {
        User.Id generatedId = generateId();
        User user = CREATE_USER_REQUEST_MAPPER.mapToUser(generatedId, createUserRequest);
        userCommandManager.create(user);

        return Response.status(Response.Status.CREATED)
                .entity(CREATE_USER_RESPONSE_MAPPER.mapToCreateUserResponse(user))
                .build();
    }

    @Override
    public Response getUser (User.Id userId) {
        User user = userQueryManager.get(userId);
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @Override
    public AccountManagementResource accountResource (User.Id userId) {
        User user = userQueryManager.get(userId);
        return resourceContext.initResource(accountManagementResourceFactory.create(user));
    }

    private User.Id generateId () {
        return User.Id.valueOf(UUID.randomUUID().toString());
    }
}
