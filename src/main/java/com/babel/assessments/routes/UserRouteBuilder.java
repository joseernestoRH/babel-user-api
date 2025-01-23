package com.babel.assessments.routes;

import com.babel.assessments.model.User;
import com.babel.assessments.repository.UserRepository;
import com.babel.assessments.exceptions.UserNotFoundException;
import com.babel.assessments.util.UserValidator;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class UserRouteBuilder extends RouteBuilder {

    @Inject
    UserRepository userRepository;

    @Override
    public void configure() {
        onException(UserNotFoundException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(exchange -> {
                    Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    Map<String, String> body = new HashMap<>();
                    body.put("error", ex.getMessage());
                    exchange.getIn().setBody(body);
                });

        onException(IllegalArgumentException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(exchange -> {
                    Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    Map<String, String> body = new HashMap<>();
                    body.put("error", ex.getMessage());
                    exchange.getIn().setBody(body);
                });

        from("direct:createUser")
                .process(exchange -> {
                    User user = exchange.getIn().getBody(User.class);
                    UserValidator.validate(user);
                    UserValidator.assignRole(user);
                    User created = userRepository.createUser(user);
                    exchange.getIn().setBody(created);
                });

        from("direct:getAllUsers")
                .process(exchange -> {
                    List<User> all = userRepository.getAll();
                    exchange.getIn().setBody(all);
                });

        from("direct:getUserById")
                .process(exchange -> {
                    UUID id = exchange.getIn().getBody(UUID.class);
                    User user = userRepository.getById(id)
                            .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
                    exchange.getIn().setBody(user);
                });

        from("direct:updateUser")
                .process(exchange -> {
                    Object[] arr = exchange.getIn().getBody(Object[].class);
                    UUID id = (UUID) arr[0];
                    User data = (User) arr[1];
                    UserValidator.validate(data);
                    UserValidator.assignRole(data);
                    User updated = userRepository.updateUser(id, data);
                    if (updated == null) {
                        throw new UserNotFoundException("Cannot update. User not found: " + id);
                    }
                    exchange.getIn().setBody(updated);
                });

        from("direct:deleteUser")
                .process(exchange -> {
                    UUID id = exchange.getIn().getBody(UUID.class);
                    if (!userRepository.deleteUser(id)) {
                        throw new UserNotFoundException("Cannot delete. User not found: " + id);
                    }
                });

        from("direct:generateCsvReport")
                .process(exchange -> {
                    List<User> users = userRepository.getAll();
                    exchange.getIn().setBody(users);
                })
                .process(exchange -> {
                    List<User> users = exchange.getIn().getBody(List.class);
                    if (users == null) {
                        users = List.of();
                    }
                    List<Map<String, Object>> csvData = users.stream()
                            .map(u -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", u.getId());
                                map.put("name", u.getName());
                                map.put("whatsapp", u.getWhatsapp());
                                map.put("email", u.getEmail());
                                map.put("role", u.getRole());
                                return map;
                            })
                            .toList();
                    exchange.getIn().setBody(csvData);
                })
                .marshal().csv();
    }
}
