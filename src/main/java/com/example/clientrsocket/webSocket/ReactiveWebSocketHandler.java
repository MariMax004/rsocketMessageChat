package com.example.clientrsocket.webSocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private final Mono<RSocket> rSocketMono;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(message -> rSocketMono.flatMapMany(rSocket ->
                        rSocket.requestStream(DefaultPayload.create(message))
                                .map(Payload::getDataUtf8)
                ))
                .map(session::textMessage)
                .as(session::send);
    }
}

