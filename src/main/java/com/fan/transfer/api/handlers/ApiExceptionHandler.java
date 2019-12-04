package com.fan.transfer.api.handlers;

import com.fan.transfer.domain.ErrorResponse;
import com.fan.transfer.services.DbException;
import com.fan.transfer.services.EntityNotFoundException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Produces({"application/json"})
public class ApiExceptionHandler implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse (Throwable exception) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        String errorMessage = "Unknown error";

        if (exception instanceof EntityNotFoundException) {
            var ex = (EntityNotFoundException) exception;
            errorMessage = ex.getLocalizedMessage();
            status = Response.Status.NOT_FOUND;
        } else if (exception instanceof IllegalArgumentException) {
            var ex = (IllegalArgumentException) exception;
            errorMessage = ex.getLocalizedMessage();
            status = Response.Status.BAD_REQUEST;
        } else if (exception instanceof DbException) {
            var ex = (DbException) exception;
            errorMessage = ex.getLocalizedMessage();
        }

        return Response
                .status(status)
                .entity(ErrorResponse.builder()
                        .error(errorMessage)
                        .build())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
