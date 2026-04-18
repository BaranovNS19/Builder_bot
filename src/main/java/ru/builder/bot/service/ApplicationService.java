package ru.builder.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.builder.bot.data.TypeApplication;
import ru.builder.bot.model.ApplicationEntity;
import ru.builder.bot.model.TimeSlotEntity;
import ru.builder.bot.repository.ApplicationsRepository;

import java.util.HashMap;
import java.util.List;

@Service
public class ApplicationService {

    private final HashMap<Long, ApplicationEntity> draftApplication = new HashMap<>();
    private final UtilsService utilsService;
    private final ApplicationsRepository applicationsRepository;
    private final TimeSlotService timeSlotService;

    @Autowired
    public ApplicationService(UtilsService utilsService, ApplicationsRepository applicationsRepository, TimeSlotService timeSlotService) {
        this.utilsService = utilsService;
        this.applicationsRepository = applicationsRepository;
        this.timeSlotService = timeSlotService;
    }

    public void addCTypeApplication(Update update, TypeApplication typeApplication) {
        Long chatId = utilsService.getUniversalChatId(update);
        if (!draftApplication.containsKey(chatId)) {
            ApplicationEntity applicationEntity = new ApplicationEntity();
            applicationEntity.setChatId(chatId);
            applicationEntity.setTypeApplication(typeApplication);
            draftApplication.put(chatId, applicationEntity);
        }
    }

    public void addTimeSlotId(Update update, TimeSlotEntity timeSlotEntity) {
        Long chatId = utilsService.getUniversalChatId(update);
        ApplicationEntity applicationEntity = draftApplication.get(chatId);
        applicationEntity.setTimeSlotId(timeSlotEntity);
        draftApplication.put(chatId, applicationEntity);
    }

    public void addAddress(Update update, String address) {
        Long chatId = utilsService.getUniversalChatId(update);
        ApplicationEntity applicationEntity = draftApplication.get(chatId);
        applicationEntity.setAddress(address);
        draftApplication.put(chatId, applicationEntity);
    }

    public String saveApplication(Update update) {
        Long chatId = utilsService.getUniversalChatId(update);
        ApplicationEntity applicationEntity = draftApplication.get(chatId);
        applicationsRepository.save(applicationEntity);
        draftApplication.remove(chatId);
        return generateTextApplication(applicationEntity);
    }

    public String generateTextApplication(ApplicationEntity applicationEntity) {
        StringBuilder sb = new StringBuilder();
        sb.append("#\uFE0F⃣Номер заявки: ").append(applicationEntity.getId()).append("\n");
        if (applicationEntity.getTypeApplication().equals(TypeApplication.REPAIR)) {
            sb.append("\uD83D\uDD16Тип заявки: ").append("Запись на ремонт объекта").append("\n");
        } else {
            sb.append("\uD83D\uDD16Тип заявки: ").append("Запись на замер").append("\n");
        }
        sb.append("\uD83D\uDCCDАдрес объета: ").append(applicationEntity.getAddress());
        return sb.toString();
    }

    public String getTextApplicationByUser(Update update) {
        StringBuilder sb = new StringBuilder();
        Long chatId = utilsService.getUniversalChatId(update);
        List<ApplicationEntity> applicationEntities = applicationsRepository.findByChatId(chatId);
        if (applicationEntities.isEmpty()) {
            return "У вас нет активных заявок";
        }
        for (ApplicationEntity a : applicationEntities) {
            TimeSlotEntity timeSlotEntity = timeSlotService.getTimeSlotById(a.getTimeSlotId().getId());
            sb.append("#\uFE0F⃣Номер заявки: ").append(a.getId()).append("\n");
            if (a.getTypeApplication().equals(TypeApplication.REPAIR)) {
                sb.append("\uD83D\uDD16Тип заявки: ").append("Запись на ремонт объекта").append("\n");
            } else {
                sb.append("\uD83D\uDD16Тип заявки: ").append("Запись на замер").append("\n");
            }
            sb.append("\uD83D\uDCCDАдрес объета: ").append(a.getAddress()).append("\n");
            sb.append("⏰Время записи: ").append(timeSlotEntity.getSlotDate()).append("  ")
                    .append(timeSlotEntity.getSlotTime()).append("\n").append("\n").append("\n");
        }
        return sb.toString();
    }
}
