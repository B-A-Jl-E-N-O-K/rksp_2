package ru.zverev.rksp4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.zverev.rksp4.model.Salad;

import java.util.List;

@RestController
@RequestMapping("/api/salad")
public class ChannelController {
    //Zverev
    private final RSocketRequester rSocketRequester;

    @Autowired
    public ChannelController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @PostMapping("/exp")
    public Flux<Salad> addSaladMultiple(@RequestBody List<Salad> saladList){
        //List<Salad> saladList = saladListWrapper.getSaladList();
        Flux<Salad> salads = Flux.fromIterable(saladList);
        return rSocketRequester
                .route("saladChannel")
                .data(salads)
                .retrieveFlux(Salad.class);
    }
}
