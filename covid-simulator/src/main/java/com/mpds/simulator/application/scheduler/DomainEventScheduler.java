package com.mpds.simulator.application.scheduler;

import com.mpds.simulator.domain.model.events.DomainEvent;
import com.mpds.simulator.domain.model.events.InfectionReported;
import com.mpds.simulator.port.adapter.kafka.DomainEventPublisherReactive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@Component
@Slf4j
@RequiredArgsConstructor
public class DomainEventScheduler {

    private final DomainEventPublisherReactive domainEventPublisherReactive;

    //Comment this in to schedule event every 10seconds
    //@Scheduled(fixedDelay = 10000)
    public void scheduleEvent(){
        log.info("Start scheduling event....");
        Long personId = new Random().nextLong();
        DomainEvent domainEvent = new InfectionReported(0L, personId, LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));

        log.info("Publishing the following event to Kafka: "+ domainEvent.toString());
        domainEventPublisherReactive.sendEvent(domainEvent).subscribe();
    }
}
