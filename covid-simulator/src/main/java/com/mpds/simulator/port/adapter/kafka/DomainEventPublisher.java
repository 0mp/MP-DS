package com.mpds.simulator.port.adapter.kafka;

import com.mpds.simulator.config.KafkaProducerProps;
import com.mpds.simulator.domain.model.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DomainEventPublisher {

    private final KafkaProducerProps kafkaProducerProps;
    private final KafkaSender<String, DomainEvent> sender;

    private final SimpleDateFormat dateFormat;

    public DomainEventPublisher(KafkaProducerProps kafkaProducerProps) {
        this.kafkaProducerProps = kafkaProducerProps;

        String BOOTSTRAP_SERVERS = this.kafkaProducerProps.getBootstrapServer();
        String CLIENT_ID_CONFIG = this.kafkaProducerProps.getClientIdConfig();
        String ACK_CONFIG = this.kafkaProducerProps.getAcksConfig();

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ProducerConfig.ACKS_CONFIG, ACK_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DomainEventSerDes.class);
        SenderOptions<String, DomainEvent> senderOptions = SenderOptions.create(props);

        sender = KafkaSender.create(senderOptions);
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }

    public Mono<Void> sendEvent(DomainEvent domainEvent) {
        ProducerRecord<String, DomainEvent> producerRecord = new ProducerRecord<>(kafkaProducerProps.getTopic(), domainEvent.getUuid().toString(), domainEvent);

        return sender.send(Mono.just(SenderRecord.create(producerRecord, domainEvent.getUuid().toString())))
                .doOnNext(r -> {
                    RecordMetadata metadata = r.recordMetadata();
                    System.out.printf("Message %s sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
                            r.correlationMetadata(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            dateFormat.format(new Date(metadata.timestamp())));
                })
                .then()
                .doOnError(e -> log.error("Sending to Kafka failed:" + e.getMessage()));
    }

    public Flux<?> publishEvents(Flux<DomainEvent> domainEventFlux) {
//        ProducerRecord<String, DomainEvent> producerRecord = new ProducerRecord<>(kafkaProducerProps.getTopic(), domainEvent.getUuid().toString(), domainEvent);

        Flux<SenderRecord<String, DomainEvent, String>> senderRecordFlux = domainEventFlux.map(domainEvent -> {
            ProducerRecord<String, DomainEvent> producerRecord = new ProducerRecord<>(kafkaProducerProps.getTopic(), domainEvent.getUuid().toString(), domainEvent);
            SenderRecord<String, DomainEvent, String> stringDomainEventUUIDSenderRecord = SenderRecord.create(producerRecord, domainEvent.getUuid().toString());
            return stringDomainEventUUIDSenderRecord;
        }).parallel().runOn(Schedulers.boundedElastic()).sequential().publishOn(Schedulers.boundedElastic());

//        Flux<ProducerRecord<String, DomainEvent>> senderRecordFlux = domainEventFlux.map(domainEvent -> {
//            ProducerRecord<String, DomainEvent> producerRecord = new ProducerRecord<>(kafkaProducerProps.getTopic(), domainEvent.getUuid().toString(), domainEvent);
//            return producerRecord;
//        });

        return sender.send(senderRecordFlux);
//                .map(tSenderResult -> {
//                    RecordMetadata metadata = tSenderResult.recordMetadata();
//                    System.out.printf("Message %s sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
//                            tSenderResult.correlationMetadata(),
//                            metadata.topic(),
//                            metadata.partition(),
//                            metadata.offset(),
//                            dateFormat.format(new Date(metadata.timestamp())));
//                    return Mono.empty();
//                    processResult(processResult(tSenderResult))
//                });

//        return sender.send(Mono.just(SenderRecord.create(producerRecord, domainEvent.getUuid().toString())))
//                .doOnNext(r -> {
//                    RecordMetadata metadata = r.recordMetadata();
//                    System.out.printf("Message %s sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
//                            r.correlationMetadata(),
//                            metadata.topic(),
//                            metadata.partition(),
//                            metadata.offset(),
//                            dateFormat.format(new Date(metadata.timestamp())));
//                })
//                .then()
//                .doOnError(e -> log.error("Sending to Kafka failed:"+  e.getMessage()));
    }

    private RecordMetadata processResult(SenderResult<String> result) {
        RecordMetadata metadata = result.recordMetadata();
        System.out.printf("Message %s sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
                metadata,
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                dateFormat.format(new Date(metadata.timestamp())));
//        Callback cb = result.correlationMetadata();
//        cb.onCompletion(metadata, null);
//        latch.countDown();
        return metadata;
    }

}
