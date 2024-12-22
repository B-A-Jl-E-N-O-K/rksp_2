package ru.zverev.rksp4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.zverev.rksp4.model.Salad;
import ru.zverev.rksp4.repository.SaladRepository;

@Controller
public class MainSocketController {
    //Zverev
    private final SaladRepository saladRepository;

    @Autowired
    public MainSocketController(SaladRepository saladRepository) {
        this.saladRepository = saladRepository;
    }

    @MessageMapping("getSalad")
    public Mono<Salad> getSalad(Long id) {
        return Mono.justOrEmpty(saladRepository.findSaladById(id));
    }

    @MessageMapping("addSalad")
    public Mono<Salad> addSalad(Salad salad) {
        return Mono.justOrEmpty(saladRepository.save(salad));
    }

    @MessageMapping("getSalads")
    public Flux<Salad> getSalads() {
        return Flux.fromIterable(saladRepository.findAll());
    }

    @MessageMapping("deleteSalad")
    public Mono<Void> deleteSalad(Long id) {
        Salad salad = saladRepository.findSaladById(id);
        saladRepository.delete(salad);
        return Mono.empty();
    }
    //Zverev
    @MessageMapping("saladChannel")
    public Flux<Salad> saladChannel(Flux<Salad> salads) {
        return salads.flatMap(salad -> Mono.fromCallable(() ->
                        saladRepository.save(salad)))
                .collectList()
                .flatMapMany(savedSalads -> Flux.fromIterable(savedSalads));
    }
}
