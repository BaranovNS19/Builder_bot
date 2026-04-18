package ru.builder.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate slotDate;
    private LocalTime slotTime;
    private Long chatId;
    @OneToOne(mappedBy = "timeSlotId")
    private ApplicationEntity application;
}
