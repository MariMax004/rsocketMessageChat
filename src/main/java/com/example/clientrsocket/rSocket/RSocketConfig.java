package com.example.clientrsocket.rSocket;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RSocketConfig {
    @Bean
    public Mono<RSocket> rSocket() {
        return RSocketFactory.connect()
                .transport(WebsocketClientTransport.create("localhost", 8000))
                .start();
    }
}
