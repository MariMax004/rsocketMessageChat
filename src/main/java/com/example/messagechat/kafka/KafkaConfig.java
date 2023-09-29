package com.example.messagechat.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;

@Slf4j
@Configuration
public class KafkaConfig {

    public static final String TOPIC = "chat-messages";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    @Bean
    public ReceiverOptions<Integer, String> receiverOptions() {
        return ReceiverOptions.<Integer, String>create()
                .subscription(Collections.singleton(TOPIC))
                .consumerProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS)
                .consumerProperty(ConsumerConfig.CLIENT_ID_CONFIG, "chat-consumer")
                .consumerProperty(ConsumerConfig.GROUP_ID_CONFIG, "chat-group")
                .consumerProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class)
                .consumerProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .consumerProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    @Bean
    public SenderOptions<Integer, String> senderOptions() {
        return SenderOptions.<Integer, String>create()
                .producerProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS)
                .producerProperty(ProducerConfig.CLIENT_ID_CONFIG, "chat-producer")
                .producerProperty(ProducerConfig.ACKS_CONFIG, "all")
                .producerProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class)
                .producerProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    }
    @Bean
    public KafkaReceiver<Integer, String> kafkaReceiver(ReceiverOptions<Integer, String> receiverOptions) {
        ReceiverOptions<Integer, String> options = receiverOptions.subscription(Collections.singleton(TOPIC))
                .addAssignListener(partitions -> log.info("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.info("onPartitionsRevoked {}", partitions));
        return KafkaReceiver.create(options);
    }

    @Bean
    public KafkaSender<Integer, String> kafkaSender(SenderOptions<Integer, String> senderOptions) {
        return KafkaSender.create(senderOptions);
    }
}