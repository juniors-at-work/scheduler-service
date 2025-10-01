package com.example.scheduler.application.usecase;

import com.example.scheduler.domain.model.AvailabilityRule;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.EventType;
import com.example.scheduler.domain.model.Slot;
import com.example.scheduler.domain.repository.AvailabilityRuleRepository;
import com.example.scheduler.domain.repository.EventRepository;
import com.example.scheduler.domain.repository.SlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateSlotsUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AvailabilityRuleRepository availabilityRuleRepository;

    @Mock
    private SlotRepository slotRepository;

    @InjectMocks
    private GenerateSlotsUseCase generateSlotsUseCase;

    @Test
    void generateSlots_singleEventType_shouldGenerateSlotsCorrectly() {
        UUID eventId = UUID.randomUUID();
        Instant startDate = LocalDate.of(2025, 7, 1)
                .atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
        Instant endDate = LocalDate.of(2025, 7, 3)
                .atStartOfDay().atZone(ZoneOffset.UTC).toInstant();

        Event event = new Event(
                eventId,
                UUID.randomUUID(),
                "Test Event",
                "Description",
                true,
                1,
                31,
                0,
                0,
                EventType.ONE2ONE,
                "slug",
                startDate,
                endDate,
                Instant.now(),
                Instant.now()
        );

        AvailabilityRule rule = new AvailabilityRule(
                UUID.randomUUID(),
                event.ownerId(),
                DayOfWeek.TUESDAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                Instant.now(),
                Instant.now()
        );

        when(eventRepository.getEventById(eventId)).thenReturn(Optional.of(event));
        when(availabilityRuleRepository.getAllRulesByUser(event.ownerId())).thenReturn(List.of(rule));

        generateSlotsUseCase.execute(eventId);

        ArgumentCaptor<List<Slot>> captor = ArgumentCaptor.forClass(List.class);
        verify(slotRepository).saveSlots(captor.capture());

        List<Slot> generatedSlots = captor.getValue();
        assertFalse(generatedSlots.isEmpty());
        assertEquals(5, generatedSlots.size());

        for (Slot slot : generatedSlots) {
            long minutes = Duration.between(slot.startTime(), slot.endTime()).toMinutes();
            assertEquals(
                    event.durationMinutes(),
                    minutes,
                    "Mismatch in slot duration: slot ID = " + slot.id() +
                            ", start = " + slot.startTime() +
                            ", end = " + slot.endTime() +
                            ", expected duration = " + event.durationMinutes() +
                            " minutes, but was = " + minutes + " minutes"
            );
        }
    }
}
