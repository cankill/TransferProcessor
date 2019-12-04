package com.fan.transfer.api.resources;

import com.fan.transfer.api.model.CreateAccountRequest;
import com.fan.transfer.api.model.TransferRequest;
import com.fan.transfer.domain.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Produces({"application/json"})
public interface AccountManagementResource {
    @POST
    @Produces({"application/json"})
    Response create (CreateAccountRequest createAccountRequest);

    @GET
    @Produces({"application/json"})
    @Path("/{accountId}/balance")
    Response getBalance (@PathParam("accountId") Account.Id accountId);

    @POST
    @Produces({"application/json"})
    @Path("/{accountId}/transfer")
    Response doTransfer (TransferRequest request);
}
