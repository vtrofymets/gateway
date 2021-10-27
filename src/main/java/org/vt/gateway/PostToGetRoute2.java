package org.vt.gateway;

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
//@Component
@RequiredArgsConstructor
@Slf4j
public class PostToGetRoute2 implements RouteLocator {

    private final RouteLocatorBuilder builder;
    private final JsonMapper jsonMapper;

    @Override
    public Flux<Route> getRoutes() {
        return builder.routes()
                .route("id", fn -> fn.path("/test")
                                                .and()
                                                .readBody(String.class, r -> true)
                        .filters(f -> f.filter((exchange, chain) -> {
                            String body = exchange.getAttribute("cachedRequestBodyObject");
                            log.info("REQUEST=[{}]", body);
                            var flood = jsonMapper.mapper(body, Flood.class);
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
}
