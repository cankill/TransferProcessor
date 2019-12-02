package com.fan.transfer.api.resources;

import com.fan.transfer.api.model.TransferRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/accauntManagement")
@Produces({"application/json"})
public interface AccauntManagementResource {
    @GET
    @Path("/balance/{userId}")
    Response getBalance(@PathParam("userId") String userId);

    @POST
    @Path("/transfer")
    Response doTransfer(TransferRequest request);
}
