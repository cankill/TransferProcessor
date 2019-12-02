package com.fan.transfer.api.resources;

import com.fan.transfer.api.model.TransferRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/accountManagement")
@Produces({"application/json"})
public interface AccountManagementResource {
    @GET
    @Path("/balance/{userId}")
    Response getBalance(@PathParam("userId") String userId);

    @POST
    @Path("/transfer")
    Response doTransfer(TransferRequest request);
}
