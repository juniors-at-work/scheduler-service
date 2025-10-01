package com.example.scheduler.adapters.mapper;

import com.example.scheduler.adapters.dto.slot.PublicSlotDto;
import com.example.scheduler.domain.model.Slot;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SlotMapper {
    PublicSlotDto toPublicSlotDto(Slot slot);

    List<PublicSlotDto> toPublicSlotDto(List<Slot> slots);
}
