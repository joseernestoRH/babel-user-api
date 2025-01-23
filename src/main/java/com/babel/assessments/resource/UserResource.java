package com.babel.assessments.resource;

import com.babel.assessments.model.User;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    ProducerTemplate template;

    @POST
    public Response createUser(User user) {
        Exchange exchange = template.request("direct:createUser",
                e -> e.getIn().setBody(user));
        int code = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, 201, Integer.class);
        if (code == 400 || code == 404) {
            Map<?,?> err = exchange.getMessage().getBody(Map.class);
            return Response.status(code).entity(err).build();
        }
        User created = exchange.getMessage().getBody(User.class);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    public Response getAllUsers() {
        Exchange exchange = template.request("direct:getAllUsers",
                e -> { /* no body needed */ });
        List<User> users = exchange.getMessage().getBody(List.class);
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") UUID id) {
        Exchange exchange = template.request("direct:getUserById",
                e -> e.getIn().setBody(id));
        int code = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, 200, Integer.class);
        if (code == 400 || code == 404) {
            Map<?,?> err = exchange.getMessage().getBody(Map.class);
            return Response.status(code).entity(err).build();
        }
        User user = exchange.getMessage().getBody(User.class);
        return Response.ok(user).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") UUID id, User user) {
        Exchange exchange = template.request("direct:updateUser",
                e -> e.getIn().setBody(new Object[]{id, user}));
        int code = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, 200, Integer.class);
        if (code == 400 || code == 404) {
            Map<?,?> err = exchange.getMessage().getBody(Map.class);
            return Response.status(code).entity(err).build();
        }
        User updated = exchange.getMessage().getBody(User.class);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") UUID id) {
        Exchange exchange = template.request("direct:deleteUser",
                e -> e.getIn().setBody(id));
        int code = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, 204, Integer.class);
        if (code == 400 || code == 404) {
            Map<?,?> err = exchange.getMessage().getBody(Map.class);
            return Response.status(code).entity(err).build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/report")
    @Produces("text/csv")
    public Response getCsvReport() {
        String csv = template.requestBody("direct:generateCsvReport", null, String.class);
        return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=users_report.csv")
                .build();
    }
}
