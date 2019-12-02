package com.fan.transfer.api.resources;

import com.fan.transfer.api.model.TransferRequest;
import com.fan.transfer.domain.User;

import javax.ws.rs.core.Response;

public class AccountManagementResourceImpl implements AccountManagementResource {
    @Override
    public Response getBalance(String userId) {
        User user = User.builder().id(userId).build();
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @Override
    public Response doTransfer(TransferRequest request) {
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
