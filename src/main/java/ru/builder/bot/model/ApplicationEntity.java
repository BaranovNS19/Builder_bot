package ru.builder.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.builder.bot.data.TypeApplication;

@Entity
@Table(name = "applications")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    @Enumerated(EnumType.STRING)
    private TypeApplication typeApplication;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "time_slot_id")
    private TimeSlotEntity timeSlotId;
    private String address;
}
