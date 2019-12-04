package com.fan.transfer.api.resources;

import com.fan.transfer.api.model.CreateUserRequest;
import com.fan.transfer.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces({"application/json"})
public interface UserManagementResource {
    @POST
    Response create (CreateUserRequest createUserRequest);

    @GET
    @Path("/{userId}")
    Response getUser (@PathParam("userId") User.Id userId);

    @Path("/{userId}/account")
    AccountManagementResource accountResource(@PathParam("userId") User.Id userId);
}
