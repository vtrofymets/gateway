package org.vt.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostToGetRoute implements RouteLocator {

    private final RouteLocatorBuilder builder;
    private final ObjectMapper objectMapper;

    @Override
    public Flux<Route> getRoutes() {
        return builder.routes()
                .route("id", fn -> fn.path("/test")
                        .and()
                        .readBody(String.class, r -> true)
                        .filters(f -> f.filter((exchange, chain) -> {
                            String body = exchange.getAttribute("cachedRequestBodyObject");
                            log.info("REQUEST=[{}]", body);
                            var flood = mapper(body, Flood.class);
                            var request = exchange.mutate()
                                    .request(b -> b.method(HttpMethod.GET)
                                            .path("/basic/" + flood.getId()))
                                    .build();
                            return chain.filter(request);
                        }))
                        .uri("http://localhost:9999/"))
                .build()
                .getRoutes();
    }

    private <T> T mapper(String body, Class<T> clazz) {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (JsonProcessingException e) {
            log.error("ERROR", e);
            throw new RuntimeException();
        }
    }

}
