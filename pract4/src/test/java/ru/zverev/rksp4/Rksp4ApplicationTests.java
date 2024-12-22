package ru.zverev.rksp4;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.rsocket.frame.decoder.PayloadDecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.zverev.rksp4.model.Salad;
import ru.zverev.rksp4.repository.SaladRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class Rksp4ApplicationTests {
	//Zverev

	@Autowired
	private SaladRepository saladRepository;
	private RSocketRequester requester;

	@BeforeEach
	public void setup() {
		requester = RSocketRequester.builder()
				.rsocketStrategies(builder -> builder.decoder(new Jackson2JsonDecoder()))
				.rsocketStrategies(builder -> builder.encoder(new Jackson2JsonEncoder()))
				.rsocketConnector(connector -> connector
						.payloadDecoder(PayloadDecoder.ZERO_COPY)
						.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2))))
				.dataMimeType(MimeTypeUtils.APPLICATION_JSON)
				.tcp("localhost", 5200);
	}

	@AfterEach
	public void cleanup() {
		requester.dispose();
	}
	//Zverev
	@Test
	public void testGetSalad() {
		Salad salad = new Salad();
		salad.setType("Cold");
		salad.setName("Olivier");
		salad.setPrice(400.0f);
		salad.setWeight(350.0f);
		salad.setIsNewYearMood(true);
		salad.setChef("Zverev A.A.");
		Salad savedSalad = saladRepository.save(salad);
		Mono<Salad> result = requester.route("getSalad")
				.data(savedSalad.getId())
				.retrieveMono(Salad.class);
		assertNotNull(result.block());
	}
	//Zverev
	@Test
	public void testAddSalad() {
		Salad salad = new Salad();
		salad.setType("Cold");
		salad.setName("Olivier");
		salad.setPrice(400.0f);
		salad.setWeight(350.0f);
		salad.setIsNewYearMood(true);
		salad.setChef("Zverev A.A.");
		Mono<Salad> result = requester.route("addSalad")
				.data(salad)
				.retrieveMono(Salad.class);
		Salad savedSalad = result.block();
		assertNotNull(savedSalad);
		assertNotNull(savedSalad.getId());
		assertTrue(savedSalad.getId() > 0);
	}
	//Zverev
	@Test
	public void testGetSalads() {
		Flux<Salad> result = requester.route("getSalads")
				.retrieveFlux(Salad.class);
		assertNotNull(result.blockFirst());
	}
	//Zverev
	@Test
	public void testDeleteSalad() {
		Salad salad = new Salad();
		salad.setType("Cold");
		salad.setName("Olivier");
		salad.setPrice(400.0f);
		salad.setWeight(350.0f);
		salad.setIsNewYearMood(true);
		salad.setChef("Zverev A.A.");
		Salad savedSalad = saladRepository.save(salad);
		Mono<Void> result = requester.route("deleteSalad")
				.data(savedSalad.getId())
				.send();
		result.block();
		Salad deletedSalad = saladRepository.findSaladById(savedSalad.getId());
		assertNotSame(deletedSalad, savedSalad);
	}
	//Zverev
	@Test
	public void testAddSalads() {
		Salad salad1 = new Salad();
		salad1.setType("Cold");
		salad1.setName("Olivier");
		salad1.setPrice(400.0f);
		salad1.setWeight(350.0f);
		salad1.setIsNewYearMood(true);
		salad1.setChef("Zverev A.A.");
		Salad salad2 = new Salad();
		salad2.setType("Cold");
		salad2.setName("Caesar");
		salad2.setPrice(500.0f);
		salad2.setWeight(250.0f);
		salad2.setIsNewYearMood(false);
		salad2.setChef("Zverev A.A.");

		List<Salad> saladList = new ArrayList<>();
		saladList.add(salad1);
		saladList.add(salad2);
		Flux<Salad> saladListFlux = Flux.fromIterable(saladList);

		Flux<Salad> result = requester.route("saladChannel")
				.data(saladListFlux)
				.retrieveFlux(Salad.class);

		List<Salad> resultList = result.collectList().block();
		assertTrue(resultList.size() > 0);
		assertNotSame(resultList.get(0), resultList.get(1));
	}

}
