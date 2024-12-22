package ru.zverev.rksp4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.zverev.rksp4.model.Salad;

@RestController
@RequestMapping("/api/salad")
public class RequestResponseController {
    //Zverev
    private final RSocketRequester rSocketRequester;

    @Autowired
    public RequestResponseController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping("/{id}")
    public Mono<Salad> getSalad(@PathVariable Long id) {
        return rSocketRequester
                .route("getSalad")
                .data(id)
                .retrieveMono(Salad.class);
    }
    //Zverev
    @PostMapping
    public Mono<Salad> addSalad(@RequestBody Salad salad) {
        return rSocketRequester
                .route("addSalad")
                .data(salad)
                .retrieveMono(Salad.class);
    }
}
