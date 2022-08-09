package id.holigo.services.holigouserservice.listeners;

import id.holigo.services.common.model.UpdateUserEmailStatusDto;
import id.holigo.services.holigouserservice.config.KafkaTopicConfig;
import id.holigo.services.holigouserservice.services.EmailStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserKafkaListener {

    private EmailStatusService emailStatusService;

    @Autowired
    public void setEmailStatusService(EmailStatusService emailStatusService) {
        this.emailStatusService = emailStatusService;
    }

    @KafkaListener(topics = KafkaTopicConfig.USER_UPDATE_EMAIL_STATUS, groupId = "user-update-email-status",
            containerFactory = "updateUserListenerContainerFactory")
    void updateUserEmailStatus(UpdateUserEmailStatusDto data) {
        switch (data.getEmailStatus()) {
            case CONFIRMED -> emailStatusService.successConfirmation(data.getUserId());
            case EXPIRED -> emailStatusService.expiryConfirmation(data.getUserId());
        }
    }
}
