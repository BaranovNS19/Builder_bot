package ru.builder.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.builder.bot.model.TimeSlotEntity;
import ru.builder.bot.repository.TimeSlotsRepository;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class TimeSlotService {

    private final TimeSlotsRepository timeSlotsRepository;

    @Autowired
    public TimeSlotService(TimeSlotsRepository timeSlotsRepository) {
        this.timeSlotsRepository = timeSlotsRepository;
    }

    public void saveRecord(TimeSlotEntity timeSlotEntity) {
        timeSlotsRepository.save(timeSlotEntity);
    }

    public TimeSlotEntity getSlotByDateAndTime(LocalDate date, LocalTime time) {
        return timeSlotsRepository.findBySlotDateAndSlotTime(date, time);
    }

    public TimeSlotEntity getTimeSlotById(Long id) {
        return timeSlotsRepository.findById(id).orElseThrow();
    }
}
