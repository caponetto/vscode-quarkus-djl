package com.caponetto.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import com.caponetto.model.text.TextRequest;
import com.caponetto.service.text.TextService;

@Path(TextResource.BASE_PATH)
public class TextResource {

    public static final String BASE_PATH = "/text";
    public static final String MODELS_PATH = "/models";
    public static final String SENTIMENT_PATH = "/sentiment";

    @Inject
    TextService service;

    @GET
    @Path(MODELS_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModels() {
        return Response.ok(service.getModels()).build();
    }

    @POST
    @Path(SENTIMENT_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sentiment(final TextRequest request) {
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            return Response.ok(service.analyzeSentiment(request.getText())).build();
        } catch (MalformedModelException | ModelNotFoundException | IOException | TranslateException e) {
            return Response.serverError().build();
        }
    }
}
