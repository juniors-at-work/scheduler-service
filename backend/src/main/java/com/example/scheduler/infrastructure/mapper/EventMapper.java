package com.example.scheduler.infrastructure.mapper;

import com.example.scheduler.adapters.dto.CreateEventRequest;
import com.example.scheduler.adapters.dto.EventFullDto;
import com.example.scheduler.adapters.dto.EventResponse;
import com.example.scheduler.adapters.dto.EventShortDto;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    String EVENT_SHARE_LINK_PATTERN = "/api/v1/public/events/%s";

    @Mapping(target = "id", source = "eventId")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "isActive", expression = "java(true)")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    Event toEntity(CreateEventRequest request, UUID ownerId, UUID eventId, String slug);

    @Mapping(target = "shareLink", source = "slug", qualifiedByName = "getShareLink")
    EventResponse toResponse(Event event);

    @Named("getShareLink")
    default String getShareLink(String slug) {
        return String.format(EVENT_SHARE_LINK_PATTERN, slug);
    }

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "ownerId", source = "event.ownerId")
    @Mapping(target = "title", source = "event.title")
    @Mapping(target = "description", source = "event.description")
    @Mapping(target = "durationMinutes", source = "event.durationMinutes")
    @Mapping(target = "bufferBeforeMinutes", source = "event.bufferBeforeMinutes")
    @Mapping(target = "bufferAfterMinutes", source = "event.bufferAfterMinutes")
    @Mapping(target = "maxParticipants", source = "event.maxParticipants")
    @Mapping(target = "isActive", source = "event.isActive")
    @Mapping(target = "eventType", source = "event.eventType")
    @Mapping(target = "slug", source = "event.slug")
    @Mapping(target = "startDate", source = "event.startDate")
    @Mapping(target = "endDate", source = "event.endDate")
    @Mapping(target = "createdAt", source = "event.createdAt")
    @Mapping(target = "updatedAt", source = "event.updatedAt")
    EventFullDto toEventFullDto(Event event);

    List<EventShortDto> toEventShortDtoList(List<Event> events);

    default Event updateEntityFromDto(Event event, CreateEventRequest dto) {
        if (event == null && dto == null) {
            return null;
        }

        UUID id = null;
        UUID ownerId = null;
        String title = null;
        String description = null;
        int durationMinutes = 0;
        int bufferBeforeMinutes = 0;
        int bufferAfterMinutes = 0;
        int maxParticipants = 0;
        boolean isActive = false;
        EventType eventType = null;
        String slug = null;
        Instant startDate = null;
        Instant endDate = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        if (event != null) {
            id = event.id();
            ownerId = event.ownerId();
            title = dto.title() == null ? event.title() : dto.title();
            description = dto.description() == null ? event.description() : dto.description();
            isActive = event.isActive();
            durationMinutes = dto.durationMinutes() == null ? event.durationMinutes()
                    : dto.durationMinutes();
            bufferBeforeMinutes = dto.bufferBeforeMinutes() == null ? event.bufferBeforeMinutes()
                    : dto.bufferBeforeMinutes();
            bufferAfterMinutes = dto.bufferAfterMinutes() == null ? event.bufferAfterMinutes()
                    : dto.bufferAfterMinutes();
            maxParticipants = dto.maxParticipants() == null ? event.maxParticipants() : dto.maxParticipants();
            eventType = dto.eventType() == null ? event.eventType() : dto.eventType();
            slug = event.slug();
            startDate = event.startDate();
            endDate = startDate.plus(durationMinutes, ChronoUnit.MINUTES);
            createdAt = event.createdAt();
            updatedAt = Instant.now();
        }

        return new Event(id, ownerId, title, description, isActive,
                maxParticipants, durationMinutes, bufferBeforeMinutes, bufferAfterMinutes,
                eventType, slug, startDate, endDate, createdAt, updatedAt);
    }
}
