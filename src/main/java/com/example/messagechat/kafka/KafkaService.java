package com.example.messagechat.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaReceiver<Integer, String> kafkaReceiver;
    private final KafkaSender<Integer, String> kafkaSender;

    public void sendToKafka(String topic, String message) {
        Flux<SenderRecord<Integer, String, Integer>> outboundFlux =
                Flux.just(SenderRecord.create(new ProducerRecord<>(topic, message), 1));
        kafkaSender.send(outboundFlux)
                .subscribe();
        log.info("send message is " + message);
    }

    public Flux<String> readFromKafka() {
        Flux<ReceiverRecord<Integer, String>> inboundFlux = kafkaReceiver.receive();
        Flux<String> records = inboundFlux
                .map(r -> {
                    String value = r.value();
                    log.info("received message is " + r.value());
                    r.receiverOffset().acknowledge();
                    return value;
                });
        return records;
    }
}
