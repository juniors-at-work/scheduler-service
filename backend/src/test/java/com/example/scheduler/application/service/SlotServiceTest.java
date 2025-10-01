package com.example.scheduler.application.service;

import com.example.scheduler.domain.fixture.TestEvents;
import com.example.scheduler.domain.fixture.TestTimeSlots;
import com.example.scheduler.domain.fixture.TestUsers;
import com.example.scheduler.domain.model.Slot;
import com.example.scheduler.domain.model.TimeInterval;
import com.example.scheduler.domain.repository.BookingRepository;
import com.example.scheduler.domain.repository.SlotRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {
    private SlotService slotService;
    @Mock
    private SlotRepository mockSlotRepository;
    @Mock
    private BookingRepository mockBookingRepository;

    @BeforeEach
    public void setUp() {
        slotService = new SlotService(mockSlotRepository, mockBookingRepository);
    }

    @Test
    public void getRemoveOneSlotTest() {
        Slot testSlot1 = TestTimeSlots.getSlots().getFirst();
        TimeInterval testSlotInterval1 = new TimeInterval(testSlot1.startTime().plusSeconds(300),
                testSlot1.endTime().plusSeconds(300));
        when(mockSlotRepository.getAllSlotsForEvent(TestEvents.demo().id())).thenReturn(TestTimeSlots.getSlots());
        when(mockBookingRepository.getTimeOfBookingsForUser(TestUsers.ALICE.id()))
                .thenReturn(List.of(testSlotInterval1));

        List<Slot> availableSlots = slotService.getAvailableSlots(TestEvents.demo().id(), TestUsers.ALICE.id());
        Assertions.assertEquals(2, availableSlots.size());
    }

    @Test
    public void allSlotsAvailableTest() {
        Slot testSlot1 = TestTimeSlots.getSlots().getLast();
        TimeInterval testSlotInterval1 = new TimeInterval(testSlot1.startTime().plusSeconds(3600),
                testSlot1.endTime().plusSeconds(3600));
        when(mockSlotRepository.getAllSlotsForEvent(TestEvents.demo().id())).thenReturn(TestTimeSlots.getSlots());
        when(mockBookingRepository.getTimeOfBookingsForUser(TestUsers.ALICE.id()))
                .thenReturn(List.of(testSlotInterval1));

        List<Slot> availableSlots = slotService.getAvailableSlots(TestEvents.demo().id(), TestUsers.ALICE.id());
        Assertions.assertEquals(3, availableSlots.size());
    }
}
