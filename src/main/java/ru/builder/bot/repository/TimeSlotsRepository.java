package ru.builder.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.builder.bot.model.TimeSlotEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TimeSlotsRepository extends JpaRepository<TimeSlotEntity, Long> {
    TimeSlotEntity findBySlotDateAndSlotTime(LocalDate localDate, LocalTime localTime);

    TimeSlotEntity findSlotById(Long id);
}
