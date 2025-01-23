package com.babel.assessments.resource;

import com.babel.assessments.model.User;
import org.apache.camel.ProducerTemplate;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    ProducerTemplate template;

    @POST
    public Response createUser(User user) {
        try {
            User created = template.requestBody("direct:createUser", user, User.class);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response getAllUsers() {
        List<User> list = template.requestBody("direct:getAllUsers", null, List.class);
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") UUID id) {
        try {
            User user = template.requestBody("direct:getUserById", id, User.class);
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") UUID id, User user) {
        try {
            Object[] body = { id, user };
            User updated = template.requestBody("direct:updateUser", body, User.class);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") UUID id) {
        try {
            template.requestBody("direct:deleteUser", id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/report")
    @Produces("text/csv")
    public Response getCsvReport() {
        try {
            String csv = template.requestBody("direct:generateCsvReport", null, String.class);
            return Response.ok(csv)
                    .header("Content-Disposition", "attachment; filename=users_report.csv")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
