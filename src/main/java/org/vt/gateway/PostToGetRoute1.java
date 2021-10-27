package org.vt.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostToGetRoute1 implements RouteLocator {

    private final RouteLocatorBuilder builder;
    private final JsonMapper jsonMapper;

    @Override
    public Flux<Route> getRoutes() {
        return builder.routes()
                .route("id", fn -> fn.path("/test")
                        .filters(f -> f.filter(((exchange, chain) -> exchange.getRequest()
                                .getBody()
                                .map(body -> {
                                    var decode = StandardCharsets.UTF_8.decode(body.asByteBuffer());
                                    return decode.toString();
                                })
                                .collect(Collectors.joining())
                                .map(x -> jsonMapper.mapper(x, Flood.class))
                                .map(flood -> exchange.mutate()
                                        .request(b -> b.method(HttpMethod.GET)
                                                .path("/basic/" + flood.getId()))
                                        .build())
                                .flatMap(chain::filter))))
                        .uri("http://localhost:9999/"))
                .build()
                .getRoutes();
    }


}
