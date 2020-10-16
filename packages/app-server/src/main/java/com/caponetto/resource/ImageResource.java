package com.caponetto.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.caponetto.model.ClassifyRequest;
import com.caponetto.model.ClassifyResponse;
import com.caponetto.service.ClassificationService;

import org.apache.commons.imaging.ImageReadException;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;

@Path("/")
public class ImageResource {

    @Inject
    ClassificationService service;

    @POST
    @Path("/classify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response classify(final ClassifyRequest request) {
        try {
            final Classifications.Classification result = service.classify(request.getPath());
            return Response
                    .ok(new ClassifyResponse(result.getClassName().substring(result.getClassName().indexOf(" ") + 1),
                            (int) (result.getProbability() * 100)))
                    .build();
        } catch (TranslateException | IOException | ModelException | ImageReadException e) {
            return Response.serverError().build();
        }
    }
}
