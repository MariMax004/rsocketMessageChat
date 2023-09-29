package com.example.messagechat.rSocket;

import com.example.messagechat.kafka.KafkaService;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RSocketService extends AbstractRSocket {

    private final KafkaService kafkaService;

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        kafkaService.sendToKafka("chat-messages", payload.getDataUtf8());
        log.info("requestStream {}");
        return kafkaService.readFromKafka().map(DefaultPayload::create);
    }


    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        log.info("fire-and-forget: server received " + payload.getDataUtf8());
        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        log.info(payload.getDataUtf8());
        return Mono.just(DefaultPayload.create("Connection successful"));
    }


    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.from(payloads).map(Payload::getDataUtf8)
                .doOnNext(str -> log.info("Received: " + str))
                .map(DefaultPayload::create);

    }
}
