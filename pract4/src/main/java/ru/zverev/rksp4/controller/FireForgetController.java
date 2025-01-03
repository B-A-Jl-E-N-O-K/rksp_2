package ru.zverev.rksp4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/salad")
public class FireForgetController {
    //Zverev
    private final RSocketRequester rSocketRequester;

    @Autowired
    public FireForgetController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteSalad(@PathVariable Long id) {
        return rSocketRequester
                .route("deleteSalad")
                .data(id)
                .send();
    }
}
