package com.mpds.simulator.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonPropertyOrder({"uuid", "personId", "eventType", "occurredOn"})
public class PersonHealed extends DomainEvent{

    private final Long personId;

    @JsonCreator
    public PersonHealed(@JsonProperty("personId") Long personId, @JsonProperty("occurredOn") LocalDateTime occurredOn) {
        super(UUID.randomUUID(), occurredOn);
        this.personId=personId;
    }

    @Override
    public String eventType() {
        return this.getClass().getSimpleName();
    }
}
