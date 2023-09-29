package com.example.messagechat.rSocket;

import com.example.messagechat.kafka.KafkaService;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class RSocketServerConfig {

    private final KafkaService kafkaService;

    @PostConstruct
    public void startServer() {
        RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(
                        new RSocketService(kafkaService)))
                .transport(WebsocketServerTransport.create("localhost", 8000))
                .start()
                .block()
                .onClose()
                .block();
    }
}

