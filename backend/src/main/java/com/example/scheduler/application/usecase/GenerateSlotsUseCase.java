package com.example.scheduler.application.usecase;

import com.example.scheduler.domain.exception.SlotGenerationException;
import com.example.scheduler.domain.model.*;
import com.example.scheduler.domain.repository.AvailabilityRuleRepository;
import com.example.scheduler.domain.repository.EventRepository;
import com.example.scheduler.domain.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GenerateSlotsUseCase {
    private final EventRepository eventRepository;
    private final AvailabilityRuleRepository availabilityRuleRepository;
    private final SlotRepository slotRepository;
    private final ZoneId zone = ZoneId.of("UTC");
    private final long DEFAULT_DAYS_TO_ADD = 60L;

    @Autowired
    public GenerateSlotsUseCase(EventRepository eventRepository,
                                AvailabilityRuleRepository availabilityRuleRepository,
                                SlotRepository slotRepository) {
        this.eventRepository = eventRepository;
        this.availabilityRuleRepository = availabilityRuleRepository;
        this.slotRepository = slotRepository;
    }

    public void execute(UUID eventId) {
        Event event = eventRepository.getEventById(eventId)
                .orElseThrow(() -> new SlotGenerationException("Event hot found"));
        List<AvailabilityRule> availabilityRules = availabilityRuleRepository.getAllRulesByUser(event.ownerId());
        if (availabilityRules.isEmpty()) {
            throw new SlotGenerationException("No available rules");
        }

        if (event.eventType().equals(EventType.ONE2ONE)) {
            slotRepository.saveSlots(generateForSingle(event, availabilityRules));
        } else {
            slotRepository.saveSlots(generateForGroup(event, availabilityRules));
        }
    }

    private List<Slot> generateForGroup(Event event, List<AvailabilityRule> rules) {
        List<Slot> slots = new ArrayList<>();

        var currDate = event.startDate().atZone(zone).toLocalDate();
        var endDate = event.endDate() == null ?
                event.startDate().atZone(zone).toLocalDate().plusDays(DEFAULT_DAYS_TO_ADD) :
                event.endDate().atZone(zone).toLocalDate();

        while (!currDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currDate.getDayOfWeek();
            for (AvailabilityRule rule : rules) {
                if (rule.weekday() == dayOfWeek) {

                    LocalTime slotStartTime = rule.startTime();
                    LocalTime slotEndTime = slotStartTime.plusMinutes(event.durationMinutes());

                    Slot slot = new Slot(null,
                            event.id(),
                            LocalDateTime.of(currDate, slotStartTime).atZone(zone).toInstant(),
                            LocalDateTime.of(currDate, slotEndTime).atZone(zone).toInstant(),
                            true);

                    slots.add(slot);
                }
            }
            currDate = currDate.plusDays(1L);
        }
        if (slots.isEmpty()) {
            throw new SlotGenerationException("No available slots could be generated for event ID: " + event.id());
        }
        return slots;
    }

    private List<Slot> generateForSingle(Event event, List<AvailabilityRule> rules) {
        List<Slot> slots = new ArrayList<>();
        ZoneId zone = ZoneId.of("UTC");

        var currDate = event.startDate().atZone(zone).toLocalDate();
        var endDate = event.endDate() == null ?
                event.startDate().atZone(zone).toLocalDate().plusDays(60L) :
                event.endDate().atZone(zone).toLocalDate();

        while (!currDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currDate.getDayOfWeek();
            for (AvailabilityRule rule : rules) {
                if (rule.weekday() == dayOfWeek) {

                    LocalTime intervalStart = rule.startTime();
                    LocalTime intervalEnd = rule.endTime();

                    LocalTime slotStartTime = intervalStart;
                    LocalTime slotEndTime = slotStartTime.plusMinutes(event.durationMinutes());

                    while (!slotEndTime.isAfter(intervalEnd)) {

                        Slot slot = new Slot(null,
                                event.id(),
                                LocalDateTime.of(currDate, slotStartTime).atZone(zone).toInstant(),
                                LocalDateTime.of(currDate, slotEndTime).atZone(zone).toInstant(),
                                true);

                        slotStartTime = slotEndTime;
                        slotEndTime = slotStartTime.plusMinutes(event.durationMinutes());
                        slots.add(slot);
                    }
                }
            }
            currDate = currDate.plusDays(1L);
        }
        return slots;
    }
}
