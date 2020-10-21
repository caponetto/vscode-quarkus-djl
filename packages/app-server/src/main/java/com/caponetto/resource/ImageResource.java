package com.caponetto.resource;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import com.caponetto.model.ImageRequest;
import com.caponetto.service.ImageService;
import org.apache.commons.imaging.ImageReadException;

@Path("/")
public class ImageResource {

    public static final String CLASSIFY_PATH = "/classify";
    public static final String DETECT_PATH = "/detect";

    @Inject
    ImageService service;

    @POST
    @Path(CLASSIFY_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response classify(final ImageRequest request) {
        if (!new File(request.getPath()).exists()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            return Response.ok(service.classify(request.getPath(), request.getTopK(), request.getThreshold())).build();
        } catch (TranslateException | IOException | ModelException | ImageReadException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path(DETECT_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response detect(final ImageRequest request) {
        if (!new File(request.getPath()).exists()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            return Response.ok(service.detect(request.getPath(), request.getTopK(), request.getThreshold())).build();
        } catch (TranslateException | IOException | ModelException e) {
            return Response.serverError().build();
        }
    }
}
