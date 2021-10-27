package org.vt.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JsonMapper {

    private final ObjectMapper objectMapper;

    public <T> T mapper(String body, Class<T> clazz) {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (JsonProcessingException e) {
            log.error("ERROR", e);
            throw new RuntimeException();
        }
    }

}
