package ru.zverev.rksp4.controller;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.zverev.rksp4.model.Salad;

@RestController
@RequestMapping("/api/salad")
public class RequestStreamController {
    //Zverev
    private final RSocketRequester rSocketRequester;

    @Autowired
    public RequestStreamController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping
    public Flux<Salad> getSalads() {
        return rSocketRequester
                .route("getSalads")
                .data(new Salad())
                .retrieveFlux(Salad.class);
    }
}
