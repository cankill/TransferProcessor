package com.fan.transfer.api.handlers;

import com.fan.transfer.domain.ErrorResponse;
import com.fan.transfer.services.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.lang.reflect.InvocationTargetException;

@Slf4j
@Produces({"application/json"})
public class ApiExceptionHandler implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse (Throwable exception) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        String errorMessage = exception.getLocalizedMessage();

        if (exception instanceof EntityNotFoundException) {
            status = Response.Status.NOT_FOUND;
        } else if (exception instanceof IllegalArgumentException) {
            status = Response.Status.BAD_REQUEST;
        }

        Throwable cause = exception.getCause();
        if(cause != null) {
            if(cause instanceof InvocationTargetException) {
                cause = ((InvocationTargetException) cause).getTargetException();
            }

            errorMessage = String.format("%s. [Cause: %s]", errorMessage, cause.getMessage());
        }

        log.error("Request to API finished with failure '{}'", errorMessage);

        return Response
                .status(status)
                .entity(ErrorResponse.builder()
                        .error(errorMessage)
                        .build())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
