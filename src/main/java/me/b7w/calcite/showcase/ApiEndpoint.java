package me.b7w.calcite.showcase;


import com.google.common.collect.ImmutableMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class ApiEndpoint {

    private final CalciteClient client;

    public ApiEndpoint(CalciteClient client) {
        this.client = client;
    }

    @PostMapping("/query-jdbc")
    public Flux<Map<String, Object>> queryJdbc(@RequestBody String query) {
        List<Map<String, Object>> records = client.queryJdbc(query, new DynamicMapper());
        return Flux.fromIterable(records);
    }

    @PostMapping(value = "/query-template", produces = "application/stream+json")
    public Flux<Map<String, Object>> queryTemplate(@RequestBody String query) {
        Stream<Map<String, Object>> records = client.queryTemplate(query, new DynamicMapper());
        return Flux.fromStream(records);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, String>> onError(Exception e) {
        e.printStackTrace();
        Map<String, String> rs = ImmutableMap.of("status", "500", "error", e.getMessage());
        return ResponseEntity.internalServerError().body(rs);
    }

}
