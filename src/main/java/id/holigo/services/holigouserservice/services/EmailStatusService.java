package id.holigo.services.holigouserservice.services;

import id.holigo.services.common.model.EmailStatusEnum;
import id.holigo.services.holigouserservice.events.EmailStatusEvent;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;

public interface EmailStatusService {

    StateMachine<EmailStatusEnum, EmailStatusEvent> successConfirmation(Long id);

    StateMachine<EmailStatusEnum, EmailStatusEvent> expiryConfirmation(Long id);
}
