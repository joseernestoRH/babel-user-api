package com.babel.assessments.routes;

import com.babel.assessments.model.User;
import com.babel.assessments.repository.UserRepository;
import com.babel.assessments.util.UserValidator;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UserRouteBuilder extends RouteBuilder {

    @Inject
    private UserRepository userRepository;

    @Override
    public void configure() {
        onException(IllegalArgumentException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("${exception.message}"));

        from("direct:createUser")
                .process(validateUser)
                .process(assignRole)
                .process(exchange -> userRepository.createUser(exchange.getIn().getBody(User.class)));

        from("direct:getAllUsers")
                .process(exchange -> exchange.getIn().setBody(userRepository.getAll()));

        from("direct:getUserById")
                .process(exchange -> {
                    UUID id = exchange.getIn().getBody(UUID.class);
                    User user = userRepository.getById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
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
                        throw new IllegalArgumentException("Cannot update. Not found: " + id);
                    }
                    exchange.getIn().setBody(updated);
                });

        from("direct:deleteUser")
                .process(exchange -> {
                    UUID id = exchange.getIn().getBody(UUID.class);
                    if (!userRepository.deleteUser(id)) {
                        throw new IllegalArgumentException("Cannot delete. Not found: " + id);
                    }
                });

        from("direct:generateCsvReport")
                .bean(UserRepository.class, "getAll")
                .process(exchange -> {
                    List<User> users = exchange.getIn().getBody(List.class);
                    if (users == null) {
                        users = List.of();
                    }
                    List<Map<String, Object>> csvData = users.stream()
                            .map(user -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", user.getId());
                                map.put("name", user.getName());
                                map.put("whatsapp", user.getWhatsapp());
                                map.put("email", user.getEmail());
                                map.put("role", user.getRole());
                                return map;
                            })
                            .toList();
                    exchange.getIn().setBody(csvData);
                })
                .marshal().csv();


    }

    private final Processor validateUser = exchange -> UserValidator.validate(exchange.getIn().getBody(User.class));
    private final Processor assignRole = exchange -> UserValidator.assignRole(exchange.getIn().getBody(User.class));
}
