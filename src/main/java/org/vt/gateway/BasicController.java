package org.vt.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("/{id}")
    public ResponseEntity<String> basicGet(@PathVariable String id) {
        return ResponseEntity.ok(id);
    }

}
